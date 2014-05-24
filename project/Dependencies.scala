import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Dependencies{


  val semWebVersion =  "0.3.1"

  val authDepth = Seq(


    "com.github.t3hnar" %% "scala-bcrypt" % "2.3"

  )

  val playModules = Seq(


    "com.typesafe" %% "play-plugins-mailer" % "2.2.0" //mailer for email confirmations
  )


  val jsDeps: Seq[ModuleID] = Seq(

    "org.scala-lang.modules.scalajs" %% "scalajs-jquery" % "0.3",

    "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.4",

    "org.scalajs" %% "scalajs-pickling" % "0.2",

    "com.scalatags" %% "scalatags" % "0.2.5-JS",

    "com.scalarx" %% "scalarx" % "0.2.4-JS",

    "org.scalax" %% "semweb" % (semWebVersion + "-JS")
  )

  val diDeps = Seq{
     "org.scaldi" %% "scaldi" % "0.3.2"
  }

  val cachingDeps = Seq(
    "io.spray" % "spray-caching" % "1.3.1"
  )



  val testDeps = Seq(
    ///ScalaTest for testing
    "org.scalatest" %% "scalatest" % "2.1.3",

    "com.typesafe.akka" %% "akka-testkit" %  "2.2.0"
  )

  val webjars = Seq(
    //"org.webjars" %% "webjars-play" % "2.2.2",  //webjars support for play2

    "org.webjars" %% "webjars-play" % "2.3.0-RC1-1",

    "org.webjars" % "codemirror" % "4.1", //codemirror

    "org.webjars" % "d3js" % "3.4.6", //visualization lib

    "org.webjars" % "d3-plugins" % "da342b6",

    "org.webjars" % "jquery" % "2.1.1",

    "org.webjars" % "jquery-ui" % "1.10.4",

    //"org.webjars" % "jquery-ui-themes" % "1.10.3",

    "org.webjars" % "Semantic-UI" % "0.17.0",


    //"org.webjars" % "jquery-file-upload" % "9.5.4",

    //"org.webjars" % "select2" % "3.4.5", //autocompletion

    "org.webjars" % "pdf-js" % "0.8.1170", //PDFS

    //"org.webjars" % "famfamfam-flags" % "0.0"

    "org.webjars" % "requirejs" % "2.1.11-1",

    "org.webjars" % "ckeditor" % "4.3.4"
  )



//  val reflectDep =     scalaVersion("org.scala-lang" % "scala-reflect" % _)
//
//  val compilerDep =   scalaVersion("org.scala-lang" % "scala-compiler" % _)



  val rdfDeps = Seq(
    "com.bigdata" % "bigdata" % "1.3.0",

    "org.openrdf.sesame" % "sesame-model" % "2.7.11",

    "org.scalax" %% "semweb-sesame" % semWebVersion

  )

  val graphDeps = Seq(


    "com.assembla.scala-incubator" %% "graph-core" % "1.8.1",

    "com.assembla.scala-incubator" %% "graph-json" % "1.8.0"


  )

  val miscDeps = Seq(

    "com.github.nscala-time" %% "nscala-time" % "0.8.0"

  )

  val scalaVer = "2.10.4"

  val scalajsResolver: URLRepository = Resolver.url("scala-js-releases",  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))( Resolver.ivyStylePatterns)

  val scalaxResolver = bintray.Opts.resolver.repo("scalax", "scalax-releases")

  val denigmaResolver = bintray.Opts.resolver.repo("denigma", "denigma-releases")


}
