/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt

import sbt.Keys._
import sbt._

/**
 * The plugin that adds Play Filters as a default.
 *
 * To disable this, use sbt's disablePlugins mechanism:
 *
 * {{{
 * lazy val root = project.in(file(".")).enablePlugins(PlayScala).disablePlugins(PlayFilters)
 * }}}
 */
object PlayFilters26 extends AutoPlugin {
  override def requires = Play26
  override def trigger  = allRequirements

  override def projectSettings =
    Seq(libraryDependencies += PlayImport26.filters)
}
