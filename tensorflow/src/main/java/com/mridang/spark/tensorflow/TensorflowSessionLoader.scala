package com.mridang.spark.tensorflow

import org.apache.spark.SparkFiles
import org.tensorflow.{SavedModelBundle, Session}

object TensorflowSessionLoader {

  lazy final val tfSession: Session = SavedModelBundle.load(SparkFiles.get("model"), "serve")
    .session();

}
