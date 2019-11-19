/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt

import sbt._

import play.dev.filewatch.FileWatchService

/**
 * Declares the default imports for Play plugins.
 */
object PlayImport26 extends PlayImportCompat26 {

  val Production = config("production")

  def component(id: String) = "com.typesafe.play" %% id % play.core.PlayVersion.current

  def movedExternal(msg: String): ModuleID = {
    System.err.println(msg)
    class ComponentExternalisedException extends RuntimeException(msg) with FeedbackProvidedException
    throw new ComponentExternalisedException
  }

  val playCore = component("play")

  val nettyServer = component("play-netty-server")

  val akkaHttpServer = component("play-akka-http-server")

  val logback = component("play-logback")

  val evolutions = component("play-jdbc-evolutions")

  val jdbc = component("play-jdbc")

  def anorm =
    movedExternal(
      """Anorm has been moved to an external module.
        |See https://playframework.com/documentation/2.4.x/Migration24 for details.""".stripMargin
    )

  val javaCore = component("play-java")

  val javaForms = component("play-java-forms")

  val jodaForms = component("play-joda-forms")

  val javaJdbc = component("play-java-jdbc")

  def javaEbean =
    movedExternal(
      """Play ebean module has been replaced with an external Play ebean plugin.
        |See https://playframework.com/documentation/2.4.x/Migration24 for details.""".stripMargin
    )

  val javaJpa = component("play-java-jpa")

  val filters = component("filters-helpers")

  @deprecated("Use ehcache for ehcache implementation, or cacheApi for just the API", since = "2.6.0")
  val cache = component("play-ehcache")

  // Integration with JSR 107
  val jcache = component("play-jcache")

  val cacheApi = component("play-cache")

  val ehcache = component("play-ehcache")

  def json = movedExternal("""play-json module has been moved to a separate project.
                             |See https://playframework.com/documentation/2.6.x/Migration26 for details.""".stripMargin)

  val guice = component("play-guice")

  val ws = component("play-ahc-ws")

  // alias javaWs to ws
  val javaWs = ws

  val openId = component("play-openid")

  val specs2 = component("play-specs2")

  object PlayKeys26 {
    val playDefaultPort26    = SettingKey[Int]("playDefaultPort26", "The default port that Play runs on")
    val playDefaultAddress26 = SettingKey[String]("playDefaultAddress26", "The default address that Play runs on")

    /** Our means of hooking the run task with additional behavior. */
    val playRunHooks26 =
      TaskKey[Seq[PlayRunHook26]]("playRunHooks26", "Hooks to run additional behaviour before/after the run task")

    /** A hook to configure how play blocks on user input while running. */
    val playInteractionMode26 =
      SettingKey[PlayInteractionMode]("playInteractionMode26", "Hook to configure how Play blocks when running")

    val externalizeResources26 = SettingKey[Boolean](
      "playExternalizeResources26",
      "Whether resources should be externalized into the conf directory when Play is packaged as a distribution."
    )
    val playExternalizedResources26 =
      TaskKey[Seq[(File, String)]]("playExternalizedResources26", "The resources to externalize")
    val playJarSansExternalized26 =
      TaskKey[File]("playJarSansExternalized26", "Creates a jar file that has all the externalized resources excluded")

    val playOmnidoc26    = SettingKey[Boolean]("playOmnidoc26", "Determines whether to use the aggregated Play documentation")
    val playDocsName26   = SettingKey[String]("playDocsName26", "Artifact name of the Play documentation")
    val playDocsModule26 = SettingKey[Option[ModuleID]]("playDocsModule26", "Optional Play documentation dependency")
    val playDocsJar26    = TaskKey[Option[File]]("playDocsJar26", "Optional jar file containing the Play documentation")

    val playPlugin26 = SettingKey[Boolean]("playPlugin26")

    val devSettings26 = SettingKey[Seq[(String, String)]]("playDevSettings26")

    val generateSecret26 = TaskKey[String]("playGenerateSecret26", "Generate a new application secret", KeyRanks.BTask)
    val updateSecret26 =
      TaskKey[File]("playUpdateSecret26", "Update the application conf to generate an application secret", KeyRanks.BTask)

    val assetsPrefix26      = SettingKey[String]("assetsPrefix26")
    val playPackageAssets26 = TaskKey[File]("playPackageAssets26")

    val playMonitoredFiles26 = TaskKey[Seq[File]]("playMonitoredFiles26")
    val fileWatchService26 =
      SettingKey[FileWatchService]("fileWatchService26", "The watch service Play uses to watch for file changes")

    val includeDocumentationInBinary26 =
      SettingKey[Boolean]("includeDocumentationInBinary26", "Includes the Documentation inside the distribution binary.")
  }
}
