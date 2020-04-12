package com.github.wirtsleg.etf.spark

import java.net.URI
import java.time.LocalDate

import com.mongodb.spark._
import com.mongodb.spark.config._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.bson._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

case class HistoricalPrices(symbol: String, historical: List[Price])

case class Price(
                  date: String,
                  open: Double,
                  high: Double,
                  low: Double,
                  close: Double,
                  adjClose: Double,
                  volume: Double,
                  unadjustedVolume: Double,
                  change: Double,
                  changePercent: Double,
                  vwap: Double,
                  label: String,
                  changeOverTime: Double)

case class EtfYearResult(symbol: String, year: Int, profitPercentage: Double)

case class EtfYearResults(_id: String, yearResults: List[YearResult])

case class WrappedEtfYearResult(symbol: String, yearResult: YearResult)

case class EtfOverallInfo(_id: String, averageProfitPercentage: Double, yearResults: List[YearResult])

case class YearResult(year: Int, profitPercentage: Double)

object EtfSummary {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("spark://0.0.0.0:7077")
      .appName("etf_summary")
      .config("spark.jars", "/jobs/etf_job-assembly-0.0.1.jar")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._

    val files = FileSystem.get(new URI("hdfs://namenode:9000"), spark.sparkContext.hadoopConfiguration)
      .listFiles(new Path("/topics/etf.price/partition=0"), false)

    while (files.hasNext) {
      val filename = files.next().getPath.toString
      println(s"Processing file: $filename")

      val etfPrices = spark.read.parquet(filename)

      val etfProfitByYear = etfPrices
        .as[HistoricalPrices]
        .flatMap(row =>
          row.historical
            .groupBy(price => LocalDate.parse(price.date).getYear)
            .values
            .map(yearPrices => {
              val startPrice = yearPrices.minBy(_.date).open
              val endPrice = yearPrices.maxBy(_.date).close
              val year = LocalDate.parse(yearPrices(0).date).getYear
              EtfYearResult(row.symbol, year, (endPrice - startPrice) / endPrice * 100)
            })
        )

      val allYearsProfits = etfProfitByYear
        .map(row => WrappedEtfYearResult(row.symbol, YearResult(row.year, row.profitPercentage)))
        .groupBy($"symbol")
        .agg(collect_list($"yearResult"))
        .withColumnRenamed("symbol", "_id")
        .withColumnRenamed("collect_list(yearResult)", "yearResults")
        .as[EtfYearResults]
        .map(row => EtfYearResults(row._id, row.yearResults.sortBy(_.year)))

      val avgProfits = etfProfitByYear
        .groupBy($"symbol")
        .avg("profitPercentage")
        .withColumnRenamed("avg(profitPercentage)", "averageProfitPercentage")

      val documents = avgProfits
        .join(allYearsProfits, avgProfits("symbol") === allYearsProfits("_id"))
        .drop("symbol")
        .as[EtfOverallInfo]

      MongoSpark.save(documents, WriteConfig(Map("uri" -> "mongodb://root:example@mongo:27017/etf.overall?authSource=admin")))
    }
  }
}
