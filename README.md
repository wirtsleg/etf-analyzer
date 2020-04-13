## Анализ биржевых фондов

Презентация:
https://docs.google.com/presentation/d/1KOE1dlCqeeYKwHkTZYG2v3znx93MB3P7TCyjmZnLqhQ/edit?usp=sharing

Документ с описание:
https://docs.google.com/document/d/1wi-toJDybtp4F0xJSupT4FJLUfV6PeuEhYUb-ccCIhU/edit?usp=sharing

Архитектура:

![Архитектура](https://github.com/wirtsleg/etf-analyzer/blob/master/schema.png?raw=true)


1. Etf-grabber (spring boot приложение) забирает данные по REST API со сторонних систем и
 отправляет их в kafka
2. kafka-connect sink отгружает данные из топиков в HDFS
3. Airflow запускает Spark-job
4. Spark-job (jar на scala) анализирует данные, формирует сводные документы по каждому фонду
 и отправляет их в mongodb
5. Metabase подключается в mongodb и визуализирует данные 
