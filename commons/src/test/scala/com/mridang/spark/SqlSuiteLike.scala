package com.mridang.spark

import org.apache.spark.sql.{SQLContext, SparkSession}

trait SqlSuiteLike { self: SparkSuiteLike =>
  implicit lazy val spark: SQLContext = SparkSession
    .builder()
    .config(sc.getConf)
    .getOrCreate()
    .sqlContext
}
