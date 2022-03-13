package com.mridang.spark.streaming.kinesis.sink.elastic

import com.mridang.spark.InitSpark
import org.apache.spark.sql.DataFrame

/**
 * A Spark example that demonstrates how structured streaming can be used to stream files 
 * from a directory and index them into Elasticsearch.
 * 
 * Each file in the directory a file containing newline-delimited JSON.
 * 
 * A real world use case would be to consume product updates from Kinesis and index
 * to OpenSearch.
 * 
 * Remember to start Elasticsearch locally using this command:
 * 
 * <pre>
 * docker run --detach --publish 9400:9200 elasticsearch:8.1.0
 * </pre>
 * 
 * Read:
 * <a>
 * https://elastic.co/guide/en/elasticsearch/hadoop/master/spark.html#spark-sql-streaming-write
 * </a>
 * 
 * @author mridang
 */
object SaveToElastic extends InitSpark {

  def main(args: Array[String]): Unit = {
    import spark.implicits._
    
    val schema = spark.read.json("/tmp/ssc/").schema
    val articleDF: DataFrame = spark.readStream
      .schema(schema)
      .json("/tmp/ssc")

    articleDF.as[Article]
      .map {
        article => article.copy(price = 99)
      }
      .writeStream
      .outputMode(outputMode = "append")
      .format(source = "es")
      .option("checkpointLocation", "/tmp")
      .start("spark/people")
      .awaitTermination()
  }
}
