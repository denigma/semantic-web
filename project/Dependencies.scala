import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Dependencies{


  val semWebVersion =  "0.1"

  val authDepth = Seq(


    "com.github.t3hnar" % "scala-bcrypt_2.10" % "2.3"

  )

  val playModules = Seq(
    "org.scaldi" %% "scaldi-play" % "0.3.1",

    "org.scaldi" %% "scaldi-akka" % "0.3.1",

    "com.typesafe" %% "play-plugins-mailer" % "2.2.0" //mailer for email confirmations
  )


  val jsDeps: Seq[ModuleID] = Seq(
    "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion,

    "org.scala-lang.modules.scalajs" %% "scalajs-jquery" % "0.3",

    "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.3",

    "org.scalajs" %% "scalajs-pickling" % "0.2",

    "com.scalatags" % "scalatags_2.10" % "0.2.4-JS",

    "com.scalarx" % "scalarx_2.10" % "0.2.3-JS",

    "org.scalax" % "semweb_2.10" % (semWebVersion + "-JS")
  )

  val diDeps = Seq{
     "org.scaldi" %% "scaldi" % "0.3"
  }



  val testDeps = Seq(
    ///ScalaTest for testing
    "org.scalatest" % "scalatest_2.10" % "2.1.0",

    "com.typesafe.akka" %% "akka-testkit" %  "2.2.0"
  )

  val webjars = Seq(
    "org.webjars" %% "webjars-play" % "2.2.2-1",  //webjars support for play2

    "org.webjars" % "codemirror" % "3.22", //codemirror

    "org.webjars" % "d3js" % "3.4.3", //visualization lib

    "org.webjars" % "d3-plugins" % "da342b6",

    "org.webjars" % "jquery" % "2.1.0-2",

    "org.webjars" % "jquery-ui" % "1.10.3",

    "org.webjars" % "jquery-ui-themes" % "1.10.3",

    "org.webjars" % "Semantic-UI" % "0.15.1", //less/css framework

    //"org.webjars" % "jquery-file-upload" % "9.5.4",

    //"org.webjars" % "select2" % "3.4.5", //autocompletion

    "org.webjars" % "pdf-js" % "0.8.1170", //PDFS

    //"org.webjars" % "famfamfam-flags" % "0.0"

    "org.webjars" % "requirejs" % "2.1.11-1",

    "org.webjars" % "ckeditor" % "4.1.2"
  )



//  val reflectDep =     scalaVersion("org.scala-lang" % "scala-reflect" % _)
//
//  val compilerDep =   scalaVersion("org.scala-lang" % "scala-compiler" % _)



  val rdfDeps = Seq(
    "com.bigdata" % "bigdata" % "1.3.0",

    "org.openrdf.sesame" % "sesame-model" % "2.7.10",

    "org.scalax" % "semweb-sesame_2.10" % "0.1"

  )

  val graphDeps = Seq(


    "com.assembla.scala-incubator" % "graph-core_2.10" % "1.8.0",

    "com.assembla.scala-incubator" % "graph-json_2.10" % "1.8.0"


  )

  val miscDeps = Seq(

    "com.github.nscala-time" %% "nscala-time" % "0.8.0"

  )

  val scalaVer = "2.10.4"

  val scalajsResolver: URLRepository = Resolver.url("scala-js-releases",  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))( Resolver.ivyStylePatterns)
  val scalaxResolver = bintray.Opts.resolver.repo("scalax", "scalax-releases")

}
