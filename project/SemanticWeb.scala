import net.litola.SassPlugin
import play.Project._
import sbt.Keys._
import sbt._

trait SemanticWeb extends Collaboration with SemanticData{

  override  def appCollaborationPath = "./collaboration"

  override def semanticDataAppPath = "./semantic-data"




//  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"


  val appName         = "denigma"
  val appVersion      = "0.03"



  val appDependencies: Seq[ModuleID] = Dependencies.authDepth

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

  ).settings( play.Project.playScalaSettings ++
    SassPlugin.sassSettings ++
    //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass")):_* )
    Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass", "-r","singularitygs")):_* )
    .dependsOn(semanticData)
}


