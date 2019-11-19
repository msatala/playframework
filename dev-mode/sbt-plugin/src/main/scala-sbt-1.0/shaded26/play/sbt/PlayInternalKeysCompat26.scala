/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt

import sbt.TaskKey

/**
 * Fix compatibility issues for PlayInternalKeys. This is the version compatible with sbt 0.13.
 */
private[sbt] trait PlayInternalKeysCompat26 {
  val playReload26 = TaskKey[sbt.internal.inc.Analysis](
    "playReload26",
    "Executed when sources of changed, to recompile (and possibly reload) the app"
  )
  val playCompileEverything26 = TaskKey[Seq[sbt.internal.inc.Analysis]](
    "playCompileEverything26",
    "Compiles this project and every project it depends on."
  )
  val playAssetsWithCompilation26 = TaskKey[sbt.internal.inc.Analysis](
    "playAssetsWithCompilation26",
    "The task that's run on a particular project to compile it. By default, builds assets and runs compile."
  )
}
