import bintray.Plugin._
import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

trait ScalaJS {

  //lazy val sharedScalaSetting = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "scala"
  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "scala-js-frontend",
      scalaVersion:=Dependencies.scalaVer,
      scalacOptions ++= Seq( "-feature", "-language:_" ),
      version := "0.0.2",
      ScalaJSKeys.relativeSourceMaps := true,
     //unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "models" / "src" / "main" / "scala",
      libraryDependencies ++= Dependencies.jsDeps,
      resolvers +=  Dependencies.scalajsResolver,
      resolvers +=  Dependencies.denigmaResolver,
      libraryDependencies += "org.denigma" %% "binding" % "0.1.1"
    )

  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  ) settings (scalajsSettings: _*) dependsOn models



  lazy val modelsSettings =     scalaJSSettings ++ bintraySettings ++Seq(
    name := "models",
    scalaVersion:=Dependencies.scalaVer,
    scalacOptions ++= Seq( "-feature", "-language:_" ),
    version := "0.0.1",

    ScalaJSKeys.relativeSourceMaps := true,
    //sharedScalaModels,
    libraryDependencies ++= Dependencies.jsDeps,
    resolvers +=  Dependencies.scalajsResolver,
    resolvers += Dependencies.scalaxResolver
  )


  lazy val models = Project(
    id   = "models",
    base = file("models")
  ) settings ( modelsSettings : _*) //dependsOn scalaSemantic

}
