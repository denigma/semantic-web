import bintray.Opts
import com.typesafe.sbt.packager.universal.UniversalKeys
import play.Play._
import play._
import sbt.Keys._
import sbt._

import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._


/**
 * this files is intended to build the main project
 * it contains links to all dependencies that are needed
 * */
object Build extends sbt.Build with SemanticData  with UniversalKeys{

  override def semanticDataAppPath = "./semantic-data"

  //  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"

  //lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")


  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  )  dependsOn models

  lazy val models = Project(
    id   = "models",
    base = file("models")
  )

  val scalaVer = "2.11.2"

  val semWebVersion =  "0.6.11"

  val macwireVersion = "0.7.1"

  val scalajsResolver: URLRepository = Resolver.url("scala-js-releases",  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))( Resolver.ivyStylePatterns)

  val scalaxResolver = Opts.resolver.repo("scalax", "scalax-releases")

  val denigmaResolver = Opts.resolver.repo("denigma", "denigma-releases")


val sameSettings = Seq(

  scalaVersion := scalaVer,

  organization := "org.denigma",

	resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",

	resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/",

	resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/",

	resolvers +=  scalajsResolver,

	resolvers += scalaxResolver,

	resolvers +=  denigmaResolver
)

  val bindingVersion = "0.5.6"


  lazy val sharedModels = unmanagedSourceDirectories in Compile += baseDirectory.value / "models" / "src" / "main" / "scala"

  // Use reflection to rename the 'start' command to 'play-start'
  Option(play.Play.playStartCommand.getClass.getDeclaredField("name")) map { field =>
    field.setAccessible(true)
    field.set(playStartCommand, "play-start")
  }

  // The new 'start' command optimises the JS before calling the Play 'start' renamed 'play-start'
  val preStartCommand = Command.args("start", "<port>") { (state: State, args: Seq[String]) =>
    Project.runTask(fullOptJS in (scalajs, Compile), state)
    state.copy(remainingCommands = ("play-start " + args.mkString(" ")) +: state.remainingCommands)
  }


  lazy val semanticWebSettings = Seq(

      sharedModels,

    ScalaJSKeys.relativeSourceMaps := true, //just in case as sourcemaps do not seem to work=(

      parallelExecution in Test := false,

      scalajsOutputDir := (crossTarget in Compile).value / "classes" / "public" / "javascripts",

      //scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (scalajs, Compile)),


      stage <<= stage dependsOn (fullOptJS in (scalajs, Compile)),

      dist <<= dist dependsOn (fullOptJS in (scalajs, Compile)),

      watchSources <++= (sourceDirectory in (scalajs, Compile)).map { path => (path ** "*.scala").get}
      //incOptions := incOptions.value.withNameHashing(true)

    ) ++ (   Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, /*packageLauncher,*/ fastOptJS, fullOptJS) map { packageJSKey =>
    crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value }   )

  lazy val main = (project in file("."))
    .enablePlugins(PlayScala/*,SbtWeb*/)
    .settings(semanticWebSettings: _*)
    .dependsOn(semanticData).aggregate(scalajs)

}





