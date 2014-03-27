import sbt._
import sbt.Keys._

trait SemanticData extends Macroses {

  //lazy val banana =  RootProject(uri("git://github.com/antonkulaga/banana-rdf.git#master"))

  val semanticDataAppName         = "semantic-data"
  val semanticDataAppVersion      = "0.02"

  def semanticDataAppPath = "."

  val semanticDataAppDependencies: Seq[ModuleID] = Dependencies.graphDeps++Dependencies.rdfDeps++Dependencies.miscDeps++Dependencies.authDepth++Dependencies.testDeps

  val src = "src"


  lazy val semanticData = play.Project(semanticDataAppName, semanticDataAppVersion, semanticDataAppDependencies, path = file(this.semanticDataAppPath)).settings(
    // Add your own project settings here

    scalaVersion := Dependencies.scalaVer,
    resolvers += "Bigdata releases" at "http://systap.com/maven/releases/",
    resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/",
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
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

  ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*).dependsOn(macroses)
}



