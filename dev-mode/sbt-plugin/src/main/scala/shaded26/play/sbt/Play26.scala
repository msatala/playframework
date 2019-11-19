/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt

import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import shaded26.play.sbt.PlayImport26.PlayKeys26
import play.twirl.sbt.SbtTwirl
import sbt.Keys._
import sbt._
import shaded26.play.sbt.routes.RoutesCompiler26

/**
 * Base plugin for Play services (microservices).
 *
 * NOTE: This plugin is considered experimental and the API may change without notice.
 */
object PlayService26 extends AutoPlugin {

  override def requires = JavaServerAppPackaging

//  val autoImport = PlayImport26
  object autoImport

  override def projectSettings =
    PlaySettings26.serviceSettings ++
      Seq(
        scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
        javacOptions in Compile ++= Seq("-encoding", "utf8", "-g")
      )
}

/**
 * Base plugin for Play projects. Declares common settings for both Java and Scala based Play projects.
 */
object Play26 extends AutoPlugin {

  override def requires = SbtTwirl && SbtJsTask && RoutesCompiler26 && JavaServerAppPackaging

//  val autoImport = PlayImport26
  object autoImport

  override def projectSettings =
    PlaySettings26.defaultSettings ++
      Seq(
        scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
        javacOptions in Compile ++= Seq("-encoding", "utf8", "-g")
      )
}

/**
 * The main plugin for minimal Play Java projects that do not include Forms.
 *
 * To use this the plugin must be made available to your project
 * via sbt's enablePlugins mechanism e.g.:
 *
 * {{{
 *   lazy val root = project.in(file(".")).enablePlugins(PlayMinimalJava)
 * }}}
 */
object PlayMinimalJava26 extends AutoPlugin {
  override def requires = Play26
  override def projectSettings =
    PlaySettings26.minimalJavaSettings ++
      Seq(libraryDependencies += PlayImport26.javaCore)
}

/**
 * The main plugin for Play Java projects.
 *
 * To use this the plugin must be made available to your project
 * via sbt's enablePlugins mechanism e.g.:
 *
 * {{{
 *   lazy val root = project.in(file(".")).enablePlugins(PlayJava)
 * }}}
 */
object PlayJava26 extends AutoPlugin {
  override def requires = Play26
  override def projectSettings =
    PlaySettings26.defaultJavaSettings ++
      Seq(libraryDependencies += PlayImport26.javaForms)
}

/**
 * The main plugin for Play Scala projects. To use this the plugin must be made available to your project
 * via sbt's enablePlugins mechanism e.g.:
 * {{{
 *   lazy val root = project.in(file(".")).enablePlugins(PlayScala)
 * }}}
 */
object PlayScala26 extends AutoPlugin {
  override def requires = Play26
  override def projectSettings =
    PlaySettings26.defaultScalaSettings
}

/**
 * This plugin enables the Play netty http server
 */
object PlayNettyServer26 extends AutoPlugin {
  override def requires = Play26

  override def projectSettings = Seq(
    libraryDependencies ++= {
      if (PlayKeys26.playPlugin26.value) {
        Nil
      } else {
        Seq(PlayImport26.nettyServer)
      }
    }
  )
}

/**
 * This plugin enables the Play akka http server
 */
object PlayAkkaHttpServer26 extends AutoPlugin {
  override def requires = Play26
  override def trigger  = allRequirements

  override def projectSettings = Seq(
    libraryDependencies += PlayImport26.akkaHttpServer
  )
}

object PlayAkkaHttp2Support26 extends AutoPlugin {
  import com.lightbend.sbt.javaagent.JavaAgent

  override def requires = PlayAkkaHttpServer26 && JavaAgent

  import JavaAgent.JavaAgentKeys._

  override def projectSettings = Seq(
    libraryDependencies += "com.typesafe.play" %% "play-akka-http2-support" % play.core.PlayVersion.current,
    javaAgents += "org.mortbay.jetty.alpn"     % "jetty-alpn-agent"         % "2.0.9" % "compile;test"
  )
}
