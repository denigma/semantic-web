import java.io.File

import bintray.Opts
import bintray.Plugin._
import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import play.Play._
import play._
import sbt.Keys._
import sbt._

import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import scala.scalajs.sbtplugin.env.phantomjs.PhantomJSEnv
import com.typesafe.sbt.web.SbtWeb.autoImport._
import com.typesafe.sbt.less.Import.LessKeys


/**
 * this files is intended to build the main project
 * it contains links to all dependencies that are needed
 * */
object Build extends sbt.Build with SemanticData  with UniversalKeys
{

  override def semanticDataAppPath = "./semantic-data"

  //  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"

  //lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")


  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  )


  val scalajsResolver: URLRepository = Resolver.url("scala-js-releases",  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))( Resolver.ivyStylePatterns)

  val scalaxResolver = Opts.resolver.repo("scalax", "scalax-releases")

  val denigmaResolver = Opts.resolver.repo("denigma", "denigma-releases")


  val sameSettings = Seq(

    scalaVersion := Versions.scala,

    organization := "org.denigma",

    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",

    resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/",

    resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/",

    resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/alexander-myltsev/maven/",

    resolvers +=  scalajsResolver,

    resolvers += scalaxResolver,

    resolvers +=  denigmaResolver
  )



  lazy val main = (project in file("."))
    .enablePlugins(PlayScala,SbtWeb)
    .settings(semanticWebSettings: _*)
    .dependsOn(semanticData).aggregate(scalajs)


  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      relativeSourceMaps := true,
      ScalaJSKeys.preLinkJSEnv := new PhantomJSEnv,
      ScalaJSKeys.postLinkJSEnv := new PhantomJSEnv
    )

  lazy val semanticWebSettings = Seq(

    parallelExecution in Test := false,

    scalajsOutputDir := (classDirectory in Compile).value / "public" / "javascripts",

    compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (scalajs, Compile)),

    dist <<= dist dependsOn (fullOptJS in (scalajs, Compile)),

    stage <<= stage dependsOn (fullOptJS in (scalajs, Compile)),

    includeFilter in (Assets, LessKeys.less) := "*.less",

    excludeFilter in (Assets, LessKeys.less) := "_*.less",

    pipelineStages := Seq(digest, gzip),

    EclipseKeys.skipParents in ThisBuild := false,

    commands += preStartCommand

  ) ++ (   Seq(packageLauncher, fastOptJS, fullOptJS) map { packageJSKey =>    crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value   }     )


  // Use reflection to rename the 'start' command to 'play-start'
  Option(play.Play.playStartCommand.getClass.getDeclaredField("name")) map { field =>
    field.setAccessible(true)
    field.set(playStartCommand, "play-start")
  }


  // The new 'start' command optimises the JS before calling the Play 'start' renamed 'play-start'
  lazy val preStartCommand = Command.args("start", "<port>") { (state: State, args: Seq[String]) =>
    Project.runTask(fullOptJS in (scalajs, Compile), state)
    state.copy(remainingCommands = ("play-start " + args.mkString(" ")) +: state.remainingCommands)
  }


}





