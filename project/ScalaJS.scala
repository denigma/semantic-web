import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

trait ScalaJS extends Binding {

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
      resolvers +=  Dependencies.scalajsResolver
    )

  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  ) settings (scalajsSettings: _*) dependsOn this.binding dependsOn models



  lazy val modelsSettings =     scalaJSSettings ++ Seq(
    name := "models",
    scalaVersion:=Dependencies.scalaVer,
    scalacOptions ++= Seq( "-feature", "-language:_" ),
    version := "0.0.1",

    ScalaJSKeys.relativeSourceMaps := true,
    //sharedScalaModels,
    libraryDependencies ++= Dependencies.jsDeps,
    resolvers +=  Dependencies.scalajsResolver
  )


  lazy val models = Project(
    id   = "models",
    base = file("models")
  ) settings ( modelsSettings : _*) dependsOn scalaSemantic



}


trait Binding extends ScalaSemantic{

  ScalaJSKeys.relativeSourceMaps := true

  //val bindingOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  lazy val bindingSettings =
    scalaJSSettings ++ Seq(
      name := "scala-js-binding",
      scalaVersion:=Dependencies.scalaVer,
      scalacOptions ++= Seq( "-feature", "-language:_" ),
      version := "0.0.2",
      ScalaJSKeys.relativeSourceMaps := true,
      libraryDependencies ++= Dependencies.jsDeps,
      libraryDependencies ++= Dependencies.diDeps,
      resolvers +=  Dependencies.scalajsResolver
    )
  lazy val binding = Project(
    id   = "binding",
    base = file("binding")
  ) settings (bindingSettings: _*) dependsOn this.jsmacro dependsOn scalaSemantic


  lazy val jsmacro = Project(
    id   = "jsmacro",
    base = file("jsmacro")
  ) settings (jsMacroSettings: _*)


  lazy val jsMacroSettings =
    scalaJSSettings ++ Seq(
      name := "scala-js-macro",
      scalacOptions ++= Seq( "-feature", "-language:_" ),
      ScalaJSKeys.relativeSourceMaps := true,
      version := "0.0.1",
      libraryDependencies ++= Dependencies.jsDeps
    )
}

trait ScalaSemantic {

  lazy val scalaSemanticSettings = scalaJSSettings ++ Seq(
    name := "scala-semantic",
    organization := "org.denigma",
    scalaVersion:=Dependencies.scalaVer,
    scalacOptions ++= Seq( "-feature", "-language:_" ),
    resolvers +=  Dependencies.scalajsResolver,
    version := "0.0.1"
  )


  /**
   * Project that deals with semanticweb stuff
   */
  lazy val scalaSemantic = Project(
    id   = "scala-semantic",
    base = file("scala-semantic")
  ) settings (this.scalaSemanticSettings: _*)
}