package com.mridang.spark.tensorflow

import org.tensorflow.Session

trait TensorflowModel[INPUT, OUTPUT] extends Serializable {

  def modelBundle: String

  def predict(session: Session, input: INPUT): OUTPUT

}
