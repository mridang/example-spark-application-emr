package com.mridang.spark.tensorflow.demo

import com.mridang.spark.InitSpark
import com.mridang.spark.tensorflow.{TensorflowModel, TensorflowSessionLoader, TensorflowSparkListener}
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.tensorflow.Session

object BatchPredictImages extends InitSpark {

  def main(args: Array[String]): Unit = {
    new BatchPredictImages(sparkContext, spark).run()
  }
}

class BatchPredictImages(@transient val sparkContext: SparkContext, @transient sparkSession: SparkSession)
    extends TensorflowSparkListener with Serializable {

  lazy val tensorflowModel: TensorflowModel[Array[Byte], Array[Float]] = ExampleModel

  override def tensorflowSession: Session = {
    TensorflowSessionLoader.tfSession
  }

  //noinspection ScalaCustomHdfsFormat
  def run(): Unit = {

    sparkContext.addSparkListener(this)
    sparkContext.addFile(tensorflowModel.modelBundle, recursive = true)

    val vectorDF = sparkSession.read
      .format("binaryFile")
      .option("pathGlobFilter", "*.jpg")
      .load("s3://tf-images/")
      .map(row => {
        val data: Array[Byte] = row.getAs[Array[Byte]]("content")
        tensorflowModel.predict(tensorflowSession, data)
      })

    println(vectorDF.rdd.toDebugString)
    println(vectorDF.show())
  }
}
