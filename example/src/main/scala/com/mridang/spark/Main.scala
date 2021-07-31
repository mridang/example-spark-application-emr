package com.mridang.spark

object Main extends InitSpark {

  def main(args: Array[String]): Unit = {
    println("SPARK VERSION = " + spark.version)
    close
  }
}
