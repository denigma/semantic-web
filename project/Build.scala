import sbt._
import sbt.Keys._
import play.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import com.typesafe.sbt.packager.universal.UniversalKeys
/**
 * this files is intended to build the main project
 * it contains links to all dependencies that are needed
 * */
object ApplicationBuild extends Build with SemanticWeb



trait SemanticWeb extends Collaboration with SemanticData with ScalaJS with UniversalKeys{

  override  def appCollaborationPath = "./collaboration"

  override def semanticDataAppPath = "./semantic-data"


  //  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"


  val appName         = "semantic-web"
  val appVersion      = "0.05"


  val appDependencies: Seq[ModuleID] =
    Dependencies.authDepth++Dependencies.webjars++
      Seq(
        filters,
        "org.scalajs" %% "scalajs-pickling" % "0.2",
        "org.scalajs" %% "scalajs-pickling-play-json" % "0.2"
      )

  lazy val sharedFromJS = unmanagedSourceDirectories in Compile += baseDirectory.value /  "scala"



  lazy val semanticWebSettings =
    play.Project.playScalaSettings ++ Seq(

      name                 := "semantic-web",

      version              := "0.05",

      scalaVersion:=Dependencies.scalaVer,

      scalacOptions ++= Seq("-feature", "-language:_"),

      sharedScalaSetting,

      resolvers +=   Dependencies.scalajsResolver,

      parallelExecution in Test := false,

      //scalajsOutputDir     := (crossTarget in Compile).value / "classes" / "public" / "javascripts",

      scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      organization := "org.denigma",

      coffeescriptOptions := Seq("native", "/usr/local/bin/coffee -p"),

      sharedFromJS,

      compile in Compile <<= (compile in Compile) dependsOn (preoptimizeJS in (scalajs, Compile)),

      dist <<= dist dependsOn (optimizeJS in (scalajs, Compile)),

      watchSources <++= (sourceDirectory in (scalajs, Compile)).map { path => (path ** "*.scala").get}

    ) ++ (
      // ask scalajs project to put its outputs in scalajsOutputDir
      Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, preoptimizeJS, optimizeJS) map {
        packageJSKey => crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      }
      ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings


  //play.Project.playScalaSettings ++ SassPlugin.sassSettings

  val main = play.Project(appName, appVersion, appDependencies).settings(semanticWebSettings: _*).dependsOn(semanticData).dependsOn(scalajs)

  //SassPlugin.sassSettings ++
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass")):_* )
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass", "-r","singularitygs")):_* )
}


trait ScalaJS extends Binding{

  //lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "scala-js-frontend",
      scalaVersion:=Dependencies.scalaVer,
      scalacOptions ++= Seq( "-feature", "-language:_" ),
      version := "0.0.2",
      sharedScalaSetting,
      libraryDependencies ++= Dependencies.jsDeps,
      resolvers +=  Dependencies.scalajsResolver
  )
  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  ) settings (scalajsSettings: _*) dependsOn this.binding

 lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
}

trait Binding {

  //lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
  //val bindingOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  lazy val bindingSettings =
    scalaJSSettings ++ Seq(
      name := "scala-js-binding",
      scalaVersion:=Dependencies.scalaVer,
      scalacOptions ++= Seq( "-feature", "-language:_" ),
      version := "0.0.2",
      libraryDependencies ++= Dependencies.jsDeps,
      resolvers +=  Dependencies.scalajsResolver
    )
  lazy val binding = Project(
    id   = "binding",
    base = file("binding")
  ) settings (bindingSettings: _*) dependsOn this.jsmacro

  lazy val jsMacroSettings =
    scalaJSSettings ++ Seq(
      name := "scala-js-macro",
      scalacOptions ++= Seq( "-feature", "-language:_" ),
      version := "0.0.1",
      libraryDependencies ++= Dependencies.jsDeps
    )

  lazy val jsmacro = Project(
    id   = "jsmacro",
    base = file("jsmacro")
  ) settings (jsMacroSettings: _*)


}


