import sbt._
import sbt.Keys._

object Dependencies {

  val authDepth = Seq(

    "jp.t2v" %% "play2-auth"      % "0.11.0",

    "jp.t2v" %% "play2-auth-test" % "0.11.0" % "test",

    "com.github.t3hnar" % "scala-bcrypt_2.10" % "2.3"
  )




  val testDeps = Seq(
    ///ScalaTest for testing
    "org.scalatest" % "scalatest_2.10" % "2.1.0-RC2",

    "com.typesafe.akka" %% "akka-testkit" %  "2.2.0"
  )

  val webjars = Seq(
    "org.webjars" %% "webjars-play" % "2.2.1-2",

    "org.webjars" % "jquery" % "2.1.0-2",

    "org.webjars" % "jquery-ui" % "1.10.3",

    "org.webjars" % "jquery-ui-themes" % "1.10.3",

    "org.webjars" % "Semantic-UI" % "0.11.0"
  )




  val reflectDep =     scalaVersion("org.scala-lang" % "scala-reflect" % _)

  val compilerDep =   scalaVersion("org.scala-lang" % "scala-compiler" % _)



  val rdfDeps = Seq(
    "com.bigdata" % "bigdata" % "1.3.0"

    //    "org.openrdf.sesame" % "sesame" % sesameVersion,
    //    "org.openrdf.sesame" % "sesame-query" % sesameVersion,
    //    "org.openrdf.sesame" % "sesame-rio-api" % sesameVersion,
    //    "org.openrdf.sesame" % "sesame-rio-turtle" % sesameVersion,
    //    "org.openrdf.sesame" % "sesame-repository-api" % sesameVersion,
    //    "org.openrdf.sesame" % "sesame-repository" % sesameVersion,
    //    "org.openrdf.sesame" % "sesame-queryalgebra-model" % sesameVersion,
    //    "org.openrdf.sesame" % "sesame-queryalgebra-evaluation" % sesameVersion,

    //  "org.topbraid" % "spin" % "1.3.1"

  )

  val graphDeps = Seq(


    "com.assembla.scala-incubator" % "graph-core_2.10" % "1.7.3",

    "com.assembla.scala-incubator" % "graph-json_2.10" % "1.7.3"


  )

  val miscDeps = Seq(

    "com.github.nscala-time" %% "nscala-time" % "0.8.0"

  )

  val scalaVer = "2.10.3"

}

//object LibVersions {
//
//
//  def src = "src"
//
//
//
//
//  val apacheCommonsVersion = "1.3.2"
//
//
//
//
//  val jenaVersion = "2.11.1"
//
//  val nScalaTimeVersion = "0.8.0"
//
//  val parboiledVersion = "2.0-M2"
//
//
//  val scalaVer = "2.10.3"
//
//  val scalaCheckVersion = "1.11.0"
//
//  //val bigDataVersion = "1.3.0" //BIGDATA doesnot support latest Sesame version
//
//
//  val scalaGraphVersion =
//
//  val scalaGraphJsonVersion = "1.7.3"
//
//  val scalaTestVersion ="2.1.0-RC2"
//
//  val scalaTimeVersion = "0.6.0"
//
//  val scalaUriVersion = "0.4.0"
//  //val sesameVersion = "2.7.10"
//  //val sesameVersion = "2.6.10" //BIGDATA doesnot support latest Sesame version
//
//  //val scalaZVersion ="7.0.5"
//
//  //val bananaVersion ="0.5-SNAPSHOT"
//
//}
//
//
