package com.mridang.spark.tensorflow

import org.apache.spark.{SparkContext, SparkFiles}
import org.apache.spark.scheduler.{SparkListener, SparkListenerApplicationEnd, SparkListenerApplicationStart}
import org.apache.spark.sql.SQLContext
import org.tensorflow.Session

trait TensorflowSparkListener extends SparkListener {

  def tensorflowModel: TensorflowModel[_, _]

  def tensorflowSession: Session

  @transient def sparkContext: SparkContext


  val sqlContext: SQLContext = new SQLContext(sparkContext)

  sparkContext.addSparkListener(listener = this)

  /**
   * Upon application start, this loads the model into the list of Spark files.
   * This means that every executor will have the Tensorflow model available in
   * one of it's temporary directories
   *
   * @param applicationStart ?
   */
  override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = {
    println("Loading provided models on executors")
    println(tensorflowModel)
    sparkContext.addFile(tensorflowModel.modelBundle, recursive = true)
    println(SparkFiles.get("saved_model.pdb"))
  }

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
    tensorflowSession.close()
    println("stopping")
  }
}
