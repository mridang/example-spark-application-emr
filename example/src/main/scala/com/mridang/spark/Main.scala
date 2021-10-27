package com.mridang.spark

//noinspection ScalaCustomHdfsFormat
object Main extends InitSpark {

  def main(args: Array[String]): Unit = {
    sqlContext.read
      .format("com.mongodb.spark.sql.DefaultSource")
      .option("collection", "merchant")
      .load()
      .show()

    close()
  }
}
