package com.mridang.spark

import com.typesafe.config.{Config, ConfigFactory}

import scala.reflect.ClassTag

object Conf {

  implicit class GenericConfig(conf: Config) {
    def optionally[T](path: String)(
      implicit classTag: ClassTag[T]): Option[T] =
      (classTag.runtimeClass, conf.hasPath(path)) match {
        case (c, true) if c == classOf[String] =>
          Some(conf.getString(path).asInstanceOf[T])
        case (c, true) if c == classOf[Boolean] =>
          Some(conf.getBoolean(path).asInstanceOf[T])
        case (_, false) => None
      }
  }

  lazy final val env: String = {
    System.getProperty("app.environment")
  }

  lazy val conf: Config = {
    Option(System.getProperty("app.environment")) match {
      case Some(name) => ConfigFactory.load().getConfig(name)
      case None => ConfigFactory.load()
    }
  }
}
