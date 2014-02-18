import sbt._
import sbt.Keys._

trait Collaboration extends SemanticData
{
  val appCollaborationName         = "collaboration"
  val appCollaborationVersion      = "0.02"

  def appCollaborationPath = "."

  override def semanticDataAppPath = "../semantic-data"



  val appCollaborationDependencies = Seq(


  )


  lazy val collaboration = play.Project(appCollaborationName, appCollaborationVersion, appCollaborationDependencies, path = file(this.appCollaborationPath)).settings(
    // Add your own project settings here

    scalaVersion := Dependencies.scalaVer,


    //compiler options
    scalacOptions ++= Seq( "-feature", "-language:_" ),

    sourceDirectory in Compile <<= baseDirectory / (src+"/main/scala"),
    sourceDirectory in Test <<= baseDirectory / (src+"/test/scala"),

    scalaSource in Compile <<= baseDirectory / (src+"/main/scala"),
    scalaSource in Test <<= baseDirectory / (src+"/test/scala"),

    javaSource in Compile <<= baseDirectory / (src+"/main/java"),
    javaSource in Test <<= baseDirectory / (src+"/test/java"),


    parallelExecution in Test := false,

    organization := "org.denigma"

  ).dependsOn(semanticData)
}