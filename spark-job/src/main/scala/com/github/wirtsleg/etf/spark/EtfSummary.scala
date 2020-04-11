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
case class EtfOverallInfo(symbol: String, averageProfitPercentage: Double)

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

          etfProfitByYear.show()

          val documents = etfProfitByYear
            .groupBy($"symbol")
            .avg("profitPercentage")
            .withColumnRenamed("avg(profitPercentage)", "averageProfitPercentage")
            .as[EtfOverallInfo]

          MongoSpark.save(documents, WriteConfig(Map("uri" -> "mongodb://root:example@mongo:27017/etf.overall?authSource=admin")))
    }
  }
}
