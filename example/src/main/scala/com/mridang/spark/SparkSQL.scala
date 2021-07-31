package com.mridang.spark

import org.apache.spark.sql.DataFrame

object SparkSQL extends InitSpark {
  def main(args: Array[String]): Unit = {
    import com.mongodb.spark._

    val df = MongoSpark.load(spark)
    df.printSchema()

    df.filter(df("age") < 100).show()

    MongoSpark.load(spark).printSchema()

    spark.catalog.listTables().show()

    val characters: DataFrame = MongoSpark.load[Character](spark)
    characters.createOrReplaceTempView("characters")

    spark.catalog.listTables().show()

    val centenarians = spark.sql("SELECT name, age FROM characters WHERE age >= 100")
    centenarians.show()
  }
}

case class Character(name: String, age: Int)

