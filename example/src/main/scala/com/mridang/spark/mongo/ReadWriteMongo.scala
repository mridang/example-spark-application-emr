package com.mridang.spark.mongo

import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import com.mridang.spark.InitSpark
import org.apache.spark.sql.DataFrame

//noinspection ScalaCustomHdfsFormat
object ReadWriteMongo extends InitSpark {

  def main(args: Array[String]): Unit = {
    import sqlContext.implicits._

    spark.catalog.listDatabases()
      .show()
    val persons: Seq[MongoPerson] = Seq(MongoPerson("moo", "mpp", 0))
    sqlContext.createDataset(persons)
      .write
      .format("com.mongodb.spark.sql.DefaultSource")
      .option("collection", "morepeeps")
      .mode("append")
      .save()

    sqlContext.read
      .format("com.mongodb.spark.sql.DefaultSource")
      .option("collection", "morepeeps")
      .load()
      .as[MongoPerson]
      .show()

    spark.catalog.listDatabases().show()
    spark.catalog.listTables().show()
    //spark.catalog.createTable()

    val readConfig = ReadConfig(Map("uri" -> "mongodb://localhost:37017/test", "collection" -> "morepeeps"), Some(ReadConfig(spark)))
    val people: DataFrame = MongoSpark.load[Peeps](spark, readConfig)
    people.show()
    people.createOrReplaceTempView("peeps")

    spark.catalog.listDatabases().show()
    spark.catalog.listTables().show()

    sqlContext.sql("SELECT * FROM peeps")
      .as[MongoPerson]
      .show()

    val characters: DataFrame = MongoSpark.load[Character](spark, ReadConfig(Map("uri" -> "mongodb://localhost:37017/test", "collection" -> "myCollection", "readPreference.name" -> "secondaryPreferred"), Some(ReadConfig(spark))))
    characters.createOrReplaceTempView("characters")

    spark.sql("SELECT name, age FROM characters WHERE age >= 100").show()
    spark.sql("SELECT * FROM peeps").show()

    close
  }
}

case class Character(name: String, age: Int)
