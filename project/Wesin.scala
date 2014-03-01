import sbt._
import sbt.Keys._

trait Wesin{


  lazy val wesin = Project("wesin", file("wesin") ).settings(

    scalaVersion:=Dependencies.scalaVer,

    //libraryDependencies <++= Dependencies.compilerDeps

    libraryDependencies ++= Seq(
      "commons-configuration" % "commons-configuration" % "1.7",

      "org.rogach" %% "scallop" % "0.9.1" ,

      "com.typesafe" % "config" % "1.2.0",

      "org.scala-lang" % "scala-compiler" % "2.10.3" ,

      "com.assembla.scala-incubator" % "graph-core_2.10" % "1.7.3",

      "org.apache.jena" % "jena-arq" % "2.11.1" ,

      "org.scalatest" % "scalatest_2.10" % "2.1.0-RC3",

      "org.openrdf.sesame" % "sesame-model" % "2.7.10"
    )

    //libraryDependencies += "org.scala-lang" % "scala-reflect" % scala

    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
}