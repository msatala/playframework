/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

import shaded26.play.sbt.PlayScala26
import sbt.Keys._
import sbt._
import shaded26.play.sbt.test.MediatorWorkaroundPlugin26

object Common {

  val bufferLogger = new AbstractLogger {
    @volatile var messages                                     = List.empty[String]
    def getLevel                                               = Level.Error
    def setLevel(newLevel: Level.Value)                        = ()
    def setTrace(flag: Int)                                    = ()
    def getTrace                                               = 0
    def successEnabled                                         = false
    def setSuccessEnabled(flag: Boolean)                       = ()
    def control(event: ControlEvent.Value, message: => String) = ()
    def logAll(events: Seq[LogEvent])                          = events.foreach(log)
    def trace(t: => Throwable)                                 = ()
    def success(message: => String)                            = ()
    def log(level: Level.Value, message: => String) = {
      if (level == Level.Error) synchronized {
        messages = message :: messages
      }
    }
  }

  import complete.DefaultParsers._

  def simpleParser(state: State) = Space ~> any.+.map(_.mkString(""))

  def checkLogContains(msg: String): Task[Boolean] = task {
    if (!bufferLogger.messages.exists(_.contains(msg))) {
      sys.error(
        "Did not find log message:\n    '" + msg + "'\nin output:\n" + bufferLogger.messages.reverse
          .mkString("    ", "\n    ", "")
      )
    }
    true
  }

}
