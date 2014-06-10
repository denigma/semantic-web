scalaJSSettings

name := "scala-js-frontend"

Build.sameSettings

scalacOptions ++= Seq( "-feature", "-language:_" )

version := "0.2"

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies += "org.denigma" %%% "binding-models" % Build.bindingVersion

libraryDependencies += "org.denigma" %%% "binding" % Build.bindingVersion

