/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt

import sbt._
import sbt.Keys._

object PlayInternalKeys26 extends PlayInternalKeysCompat26 {
  type ClassLoaderCreator = play.runsupport.Reloader.ClassLoaderCreator

  val playDependencyClasspath26 =
    TaskKey[Classpath]("playDependencyClasspath26", "The classpath containing all the jar dependencies of the project")
  val playReloaderClasspath26 = TaskKey[Classpath](
    "playReloaderClasspath26",
    "The application classpath, containing all projects in this build that are dependencies of this project, including this project"
  )
  val playCommonClassloader26 = TaskKey[ClassLoader](
    "playCommonClassloader26",
    "The common classloader, is used to hold H2 to ensure in memory databases don't get lost between invocations of run"
  )
  val playDependencyClassLoader26 = TaskKey[ClassLoaderCreator](
    "playDependencyClassloader26",
    "A function to create the dependency classloader from a name, set of URLs and parent classloader"
  )
  val playReloaderClassLoader26 = TaskKey[ClassLoaderCreator](
    "playReloaderClassloader26",
    "A function to create the application classloader from a name, set of URLs and parent classloader"
  )

  val playStop26 = TaskKey[Unit]("playStop26", "Stop Play, if it has been started in non blocking mode")

  val playAllAssets26 = TaskKey[Seq[(String, File)]]("playAllAssets", "Compiles all assets for all projects")
  val playPrefixAndAssets26 =
    TaskKey[(String, File)]("playPrefixAndAssets26", "Gets all the assets with their associated prefixes")
  val playAssetsClassLoader26 = TaskKey[ClassLoader => ClassLoader](
    "playAssetsClassloader26",
    "Function that creates a classloader from a given parent that contains all the assets."
  )
}
