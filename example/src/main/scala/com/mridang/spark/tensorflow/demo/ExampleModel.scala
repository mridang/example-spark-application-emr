package com.mridang.spark.tensorflow.demo

import com.mridang.spark.tensorflow.TensorflowModel
import org.tensorflow.Session
import org.tensorflow.ndarray.{NdArrays, Shape}
import org.tensorflow.ndarray.impl.buffer.raw.RawDataBufferFactory
import org.tensorflow.ndarray.impl.dense.FloatDenseNdArray
import org.tensorflow.types.{TFloat32, TString}

import java.nio.file.Paths

object ExampleModel extends TensorflowModel[Array[Byte], Array[Float]] {

  lazy final val outputShape: Shape = Shape.of(1, 256)

  override def modelBundle: String = {
    Paths
      .get(this.getClass.getResource("/mllib/model/saved_model.pb").toURI)
      .getParent
      .toString
  }

  override def predict(session: Session, imageBytes: Array[Byte]): Array[Float] = {
    val result = session.runner
      .feed("Placeholder", 0, TString.tensorOfBytes(NdArrays.vectorOfObjects(imageBytes)))
      .fetch("model/global_average_pooling2d/Mean")
      .run
      .get(0)

    try {
      val buffer = new Array[Float](256)
      val floatNdArray = FloatDenseNdArray.create(RawDataBufferFactory.create(buffer, false), outputShape)
      result.asInstanceOf[TFloat32].copyTo(floatNdArray)
      buffer
    } finally if (result != null) result.close()
  }

}
