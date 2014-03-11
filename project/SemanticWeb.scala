import play.Project._
import sbt.Keys._
import sbt._
//import net.litola.SassPlugin

trait SemanticWeb extends Collaboration with SemanticData{

  override  def appCollaborationPath = "./collaboration"

  override def semanticDataAppPath = "./semantic-data"


//  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"


  val appName         = "denigma"
  val appVersion      = "0.04"



  val appDependencies: Seq[ModuleID] = Dependencies.authDepth++Dependencies.webjars

  //play.Project.playScalaSettings ++ SassPlugin.sassSettings

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here

    scalaVersion:=Dependencies.scalaVer,

    scalacOptions ++= Seq("-feature", "-language:_"),

    parallelExecution in Test := false,

    organization := "org.denigma",

    coffeescriptOptions := Seq("native", "/usr/local/bin/coffee -p")

//    javaOptions in Test += testOptions,

//    scalacOptions in Test += testOptions

  ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*).dependsOn(semanticData)

  //SassPlugin.sassSettings ++
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass")):_* )
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass", "-r","singularitygs")):_* )
}


