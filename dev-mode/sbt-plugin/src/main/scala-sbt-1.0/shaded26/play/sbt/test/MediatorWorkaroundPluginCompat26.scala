/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt.test

import sbt.Keys.scalaModuleInfo
import sbt.Keys.sbtPlugin
import sbt.AutoPlugin

private[test] trait MediatorWorkaroundPluginCompat26 extends AutoPlugin {

  override def projectSettings = Seq(
    scalaModuleInfo := { scalaModuleInfo.value.map { _.withOverrideScalaVersion(sbtPlugin.value) } }
  )
}
