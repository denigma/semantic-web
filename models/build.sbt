import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys

scalaJSSettings

Build.sameSettings

name := "models"

version := "0.2"

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies += "org.denigma" %%% "binding" % "0.3.1"

libraryDependencies += "org.scalax" %%% "semweb" % Build.semWebVersion

