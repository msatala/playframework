/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt

import java.net.InetSocketAddress

/**
 * The represents an object which "hooks into" play run, and is used to
 * apply startup/cleanup actions around a play application.
 */
trait PlayRunHook26 extends play.runsupport.RunHook

object PlayRunHook26 {

  def makeRunHookFromOnStarted(f: (java.net.InetSocketAddress) => Unit): PlayRunHook26 = {
    // We create an object for a named class...
    object OnStartedPlayRunHook extends PlayRunHook26 {
      override def afterStarted(addr: InetSocketAddress): Unit = f(addr)
    }
    OnStartedPlayRunHook
  }

  def makeRunHookFromOnStopped(f: () => Unit): PlayRunHook26 = {
    object OnStoppedPlayRunHook extends PlayRunHook26 {
      override def afterStopped(): Unit = f()
    }
    OnStoppedPlayRunHook
  }

}
