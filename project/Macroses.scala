import sbt._
import sbt.Keys._

trait Macroses{


  lazy val macroses = Project("macro", file("macro") ) settings(

    scalaVersion:=Dependencies.scalaVer,

    //libraryDependencies <++= Dependencies.compilerDeps

    libraryDependencies <+=Dependencies.reflectDep,

    libraryDependencies <+=Dependencies.compilerDep

    //libraryDependencies ++= Dependencies.jsDeps,

    ) settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
}