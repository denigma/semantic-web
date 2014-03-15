import sbt._
import Keys._
import play.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import com.typesafe.sbt.packager.universal.UniversalKeys

trait SemanticWeb extends Collaboration with SemanticData with ScalaJS with UniversalKeys{

  override  def appCollaborationPath = "./collaboration"

  override def semanticDataAppPath = "./semantic-data"


//  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"


  val appName         = "semantic-web"
  val appVersion      = "0.05"


  val appDependencies: Seq[ModuleID] =  Dependencies.authDepth++Dependencies.webjars


  lazy val semanticWebSettings =
    play.Project.playScalaSettings ++ Seq(

      name                 := "semantic-web",

      version              := "0.05",

      scalaVersion:=Dependencies.scalaVer,

      scalacOptions ++= Seq("-feature", "-language:_"),

      parallelExecution in Test := false,

      scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      organization := "org.denigma",

      coffeescriptOptions := Seq("native", "/usr/local/bin/coffee -p"),

      scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs" ,

      compile in Compile <<= (compile in Compile) dependsOn (preoptimizeJS in (scalajs, Compile)),

      dist <<= dist dependsOn (optimizeJS in (scalajs, Compile)),

      watchSources <++= (sourceDirectory in (scalajs, Compile)).map { path => (path ** "*.scala").get},

      libraryDependencies += filters
    ) ++ (
      // ask scalajs project to put its outputs in scalajsOutputDir
      Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, preoptimizeJS, optimizeJS) map {
        packageJSKey =>
          crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      }
      ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings


  //play.Project.playScalaSettings ++ SassPlugin.sassSettings

  val main = play.Project(appName, appVersion, appDependencies).settings(semanticWebSettings: _*).dependsOn(semanticData).dependsOn(scalajs)

  //SassPlugin.sassSettings ++
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass")):_* )
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass", "-r","singularitygs")):_* )
}


trait ScalaJS {

  //lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
 val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "scala-js-frontend",
      version := "0.0.1",
      libraryDependencies ++= Seq(
        "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion,
        "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.3",
        "com.scalarx" % "scalarx_2.10" % "0.2.3-JS",
        "org.scala-lang.modules.scalajs" %% "scalajs-jquery" % "0.3",
        "com.scalatags" % "scalatags_2.10" % "0.2.4-JS"

      )
    )

  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  ) settings (scalajsSettings: _*)

  //  lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
}


