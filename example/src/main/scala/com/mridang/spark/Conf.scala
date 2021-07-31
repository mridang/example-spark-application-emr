/** *****************************************************************************
  * Copyright (c) 2016 Nosto Solutions Ltd All Rights Reserved.
  * <p>
  * This software is the confidential and proprietary information of
  * Nosto Solutions Ltd ("Confidential Information"). You shall not
  * disclose such Confidential Information and shall use it only in
  * accordance with the terms of the agreement you entered into with
  * Nosto Solutions Ltd.
  * *****************************************************************************/
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
