import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys

scalaJSSettings

Build.sameSettings

name := "models"

version := "0.2.1"

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies += "org.denigma" %%% "binding-models" % Build.bindingVersion

libraryDependencies += "org.scalax" %%% "semweb" % Build.semWebVersion

