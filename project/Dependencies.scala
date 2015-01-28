import sbt._
import bintray.Opts
import sbt.Keys._

import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Versions {

  val snap = "-SNAPSHOT"

  val bigdata = "1.4.0"

  val binding = "0.7"//+snap

  val bindingModels = "0.7"

  val bindingPLay = "0.7"//+snap

  val codeMirror = "4.11"

  val macwire = "0.7.3"

  val playMailer = "2.4.0"

  val scala = "2.11.5"

  val scalaRx = "0.2.6"

  val scalajsPickling =  "0.3.1"

  val scalenium = "1.0.1"

  val selectize = "0.11.2"

  val semanticUI = "1.7.3"

  val semWeb =  "0.6.18"//+snap

  val sesame = "2.7.12"

  val sprayCache = "1.3.1"

}


object Dependencies
{


  lazy val shared = Def.setting(Seq())


  val frontend = Def.setting(shared.value++Seq(
    "org.denigma" %%% "binding" % Versions.binding

  ))

  val semanticWeb = Def.setting(shared.value ++ Seq(

   ("org.denigma" %% "binding-play" % Versions.bindingPLay).excludeAll( ExclusionRule(organization = "org.scalax") ),

   "org.scalajs" %% "scalajs-pickling-play-json" % Versions.scalajsPickling,

   "com.typesafe.play" % "play-mailer_2.11" % Versions.playMailer,

   "io.spray" % "spray-caching" % Versions.sprayCache,

    "org.webjars" %% "webjars-play" % "2.3.0-2",

   "org.webjars" % "codemirror" % "4.5",

   "org.webjars" % "jquery" % "2.1.1",

   "org.webjars" % "Semantic-UI" % Versions.semanticUI,

  "org.webjars" % "ckeditor" % "4.4.1",

  "org.webjars" % "selectize.js" % Versions.selectize,

   ("com.markatta" %%% "scalenium" % Versions.scalenium).excludeAll( ExclusionRule(organization = "org.specs2") )
  ) )

  lazy val semwebDep =    Seq(
    "org.scalax" %% "semweb" % Versions.semWeb,
    "org.scalax" %% "semweb-sesame" % Versions.semWeb
  )


  val semanticData = Def.setting(shared.value ++ Seq (

    "com.github.t3hnar" %% "scala-bcrypt" % "2.4",

    "org.scalatest" %% "scalatest" % "2.2.1",

    "com.typesafe.akka" %% "akka-testkit" % "2.3.3",

    "com.bigdata" % "bigdata" % Versions.bigdata,

    "com.assembla.scala-incubator" %% "graph-core" % "1.9.0",

    "com.pellucid" %% "framian" % "0.3.3"
    ) ++ semwebDep )
}