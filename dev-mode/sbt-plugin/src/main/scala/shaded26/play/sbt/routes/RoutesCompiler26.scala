/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package shaded26.play.sbt.routes

import play.core.PlayVersion
import shaded26.play.routes.compiler.RoutesGenerator
import shaded26.play.routes.compiler.RoutesCompilationError
import shaded26.play.routes.compiler.RoutesCompiler26.RoutesCompilerTask
import shaded26.play.routes.compiler.RoutesCompiler26.GeneratedSource
import sbt._
import sbt.Keys._
import com.typesafe.sbt.web.incremental._
import play.api.PlayException
import sbt.plugins.JvmPlugin

import scala.language.implicitConversions

object RoutesKeys26 {
  val routesCompilerTasks26 = TaskKey[Seq[RoutesCompilerTask]]("playRoutesTasks26", "The routes files to compile")
  val routes26              = TaskKey[Seq[File]]("playRoutes26", "Compile the routes files")
  val routesImport26        = SettingKey[Seq[String]]("playRoutesImports26", "Imports for the router")
  val routesGenerator26     = SettingKey[RoutesGenerator]("playRoutesGenerator26", "The routes generator")
  val generateReverseRouter26 = SettingKey[Boolean](
    "playGenerateReverseRouter26",
    "Whether the reverse router should be generated. Setting to false may reduce compile times if it's not needed."
  )
  val namespaceReverseRouter26 = SettingKey[Boolean](
    "playNamespaceReverseRouter26",
    "Whether the reverse router should be namespaced. Useful if you have many routers that use the same actions."
  )

  /**
   * This class is used to avoid infinite recursions when configuring aggregateReverseRoutes, since it makes the
   * ProjectReference a thunk.
   */
  class LazyProjectReference(ref: => ProjectReference) {
    def project: ProjectReference = ref
  }

  object LazyProjectReference {
    implicit def fromProjectReference(ref: => ProjectReference): LazyProjectReference = new LazyProjectReference(ref)
    implicit def fromProject(project: => Project): LazyProjectReference               = new LazyProjectReference(project)
  }

  val aggregateReverseRoutes26 = SettingKey[Seq[LazyProjectReference]](
    "playAggregateReverseRoutes26",
    "A list of projects that reverse routes should be aggregated from."
  )

  val InjectedRoutesGenerator = shaded26.play.routes.compiler.InjectedRoutesGenerator
  val StaticRoutesGenerator   = shaded26.play.routes.compiler.StaticRoutesGenerator
}

object RoutesCompiler26 extends AutoPlugin with RoutesCompilerCompat26 {
  import RoutesKeys26._

  override def trigger = noTrigger

  override def requires = JvmPlugin

//  val autoImport = RoutesKeys26
  object autoImport

  override def projectSettings =
    defaultSettings ++
      inConfig(Compile)(routesSettings) ++
      inConfig(Test)(routesSettings)

  def routesSettings = Seq(
    sources in routes26 := Nil,
    routesCompilerTasks26 := Def.taskDyn {

      val generateReverseRouterValue  = generateReverseRouter26.value
      val namespaceReverseRouterValue = namespaceReverseRouter26.value
      val sourcesInRoutes             = (sources in routes26).value
      val routesImportValue           = routesImport26.value

      // Aggregate all the routes file tasks that we want to compile the reverse routers for.
      aggregateReverseRoutes26.value
        .map { agg =>
          routesCompilerTasks26 in (agg.project, configuration.value)
        }
        .join
        .map {
          aggTasks: Seq[Seq[RoutesCompilerTask]] =>
            // Aggregated tasks need to have forwards router compilation disabled and reverse router compilation enabled.
            val reverseRouterTasks = aggTasks.flatten.map { task =>
              task.copy(forwardsRouter = false, reverseRouter = true)
            }

            // Find the routes compile tasks for this project
            val thisProjectTasks = sourcesInRoutes.map { file =>
              RoutesCompilerTask(
                file,
                routesImportValue,
                forwardsRouter = true,
                reverseRouter = generateReverseRouterValue,
                namespaceReverseRouter = namespaceReverseRouterValue
              )
            }

            thisProjectTasks ++ reverseRouterTasks
        }
    }.value,
    watchSources in Defaults.ConfigGlobal ++= (sources in routes26).value,
    target in routes26 := crossTarget.value / "routes" / Defaults.nameForSrc(configuration.value.name),
    routes26 := compileRoutesFiles26.value,
    sourceGenerators += Def.task(routes26.value).taskValue,
    managedSourceDirectories += (target in routes26).value
  )

  def defaultSettings = Seq(
    routesImport26 := Nil,
    aggregateReverseRoutes26 := Nil,
    // Generate reverse router defaults to true if this project is not aggregated by any of the projects it depends on
    // aggregateReverseRoutes projects.  Otherwise, it will be false, since another project will be generating the
    // reverse router for it.
    generateReverseRouter26 := Def.settingDyn {
      val projectRef   = thisProjectRef.value
      val dependencies = buildDependencies.value.classpathTransitiveRefs(projectRef)

      // Go through each dependency of this project
      dependencies
        .map { dep =>
          // Get the aggregated reverse routes projects for the dependency, if defined
          Def.optional(aggregateReverseRoutes26 in dep)(_.map(_.map(_.project)).getOrElse(Nil))

        }
        .join
        .apply { aggregated: Seq[Seq[ProjectReference]] =>
          val localProject = LocalProject(projectRef.project)
          // Return false if this project is aggregated by one of our dependencies
          !aggregated.flatten.contains(localProject)
        }
    }.value,
    namespaceReverseRouter26 := false,
    routesGenerator26 := InjectedRoutesGenerator, // changed from StaticRoutesGenerator in 2.5.0
    sourcePositionMappers += routesPositionMapper
  )

  private val compileRoutesFiles26 = Def.task[Seq[File]] {
    val log = state.value.log
    if (routesGenerator26.value.id == StaticRoutesGenerator.id) {
      log.warn(
        "StaticRoutesGenerator is deprecated. Please use InjectedRoutesGenerator or a custom router instead.\n" +
          "For more info see https://www.playframework.com/documentation/2.6.x/JavaRouting#Dependency-Injection"
      )
    }
    compileRoutes(
      routesCompilerTasks26.value,
      routesGenerator26.value,
      (target in routes26).value,
      streams.value.cacheDirectory,
      log
    )
  }

  def compileRoutes(
      tasks: Seq[RoutesCompilerTask],
      generator: RoutesGenerator,
      generatedDir: File,
      cacheDirectory: File,
      log: Logger
  ): Seq[File] = {
    val ops = tasks.map(task => RoutesCompilerOp(task, generator.id, PlayVersion.current))
    val (products, errors) = syncIncremental(cacheDirectory, ops) { opsToRun: Seq[RoutesCompilerOp] =>
      val results = opsToRun.map { op =>
        op -> shaded26.play.routes.compiler.RoutesCompiler26.compile(op.task, generator, generatedDir)
      }
      val opResults = results.map {
        case (op, Right(inputs)) => op -> OpSuccess(Set(op.task.file), inputs.toSet)
        case (op, Left(_))       => op -> OpFailure
      }.toMap
      val errors = results.collect {
        case (_, Left(e)) => e
      }.flatten
      (opResults, errors)
    }

    if (errors.nonEmpty) {
      val exceptions = errors.map {
        case RoutesCompilationError(source, message, line, column) =>
          reportCompilationError(log, RoutesCompilationException(source, message, line, column.map(_ - 1)))
      }
      throw exceptions.head
    }

    products.to[Seq]
  }

  private def reportCompilationError(log: Logger, error: PlayException.ExceptionSource) = {
    // log the source file and line number with the error message
    log.error(
      Option(error.sourceName).getOrElse("") + Option(error.line).map(":" + _).getOrElse("") + ": " + error.getMessage
    )
    Option(error.interestingLines(0)).map(_.focus).flatMap(_.headOption).map { line =>
      // log the line
      log.error(line)
      Option(error.position).map { pos =>
        // print a carat under the offending character
        val spaces = (line: Seq[Char]).take(pos).map {
          case '\t' => '\t'
          case x    => ' '
        }
        log.error(spaces.mkString + "^")
      }
    }
    error
  }

}

private case class RoutesCompilerOp(task: RoutesCompilerTask, generatorId: String, playVersion: String)

case class RoutesCompilationException(source: File, message: String, atLine: Option[Int], column: Option[Int])
    extends PlayException.ExceptionSource("Compilation error", message)
    with FeedbackProvidedException {
  def line       = atLine.map(_.asInstanceOf[java.lang.Integer]).orNull
  def position   = column.map(_.asInstanceOf[java.lang.Integer]).orNull
  def input      = IO.read(source)
  def sourceName = source.getAbsolutePath
}
