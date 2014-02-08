import sbt._
import sbt.Keys._
import play.Project._
import com.typesafe.sbt.SbtAtmosPlay.atmosPlaySettings
import org.sbtidea.SbtIdeaPlugin._
import net.litola.SassPlugin
import LibVersions._

/**
 * this files is intended to build the main project
 * it contains links to all dependencies that are needed
 * */
object ApplicationBuild extends Build with SemanticWeb
{

//app build
}

trait SemanticWeb extends SemanticData {


  override def semanticDataAppPath = "./semantic-data"
 
  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"

  val appName         = "denigma"
  val appVersion      = "0.03"



  val appDependencies: Seq[ModuleID] = Seq(
    ///Add your project dependencies here,


     "com.assembla.scala-incubator" % "graph-core_2.10" % scalaGraphVersion,

    "jp.t2v" %% "play2-auth"      % playAuthVersion,

    "jp.t2v" %% "play2-auth-test" % playAuthVersion % "test"
  )

  //play.Project.playScalaSettings ++ SassPlugin.sassSettings

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here

    scalaVersion:=scalaVer,

    scalacOptions ++= Seq("-feature", "-language:_"),

    parallelExecution in Test := false,

    organization := "org.denigma",

    coffeescriptOptions := Seq("native", "/usr/local/bin/coffee -p"),

    javaOptions in Test += testOptions,

    scalacOptions in Test += testOptions

  ).settings( play.Project.playScalaSettings ++
    SassPlugin.sassSettings ++
    //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass")):_* )
    Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass", "-r","singularitygs")):_* )
    .dependsOn(semanticData)
}

trait SemanticData extends Macro {

  //lazy val banana =  RootProject(uri("git://github.com/antonkulaga/banana-rdf.git#master"))

  val semanticDataAppName         = "semantic-data"
  val semanticDataAppVersion      = "0.01"

  def semanticDataAppPath = "."

  val semanticDataAppDependencies: Seq[ModuleID] = Seq(
    "com.bigdata" % "bigdata" % bigDataVersion,
    "com.github.nscala-time" %% "nscala-time" % nScalaTimeVersion,

//    "org.openrdf.sesame" % "sesame" % sesameVersion,
//    "org.openrdf.sesame" % "sesame-query" % sesameVersion,
//    "org.openrdf.sesame" % "sesame-rio-api" % sesameVersion,
//    "org.openrdf.sesame" % "sesame-rio-turtle" % sesameVersion,
//    "org.openrdf.sesame" % "sesame-repository-api" % sesameVersion,
//    "org.openrdf.sesame" % "sesame-repository" % sesameVersion,
//    "org.openrdf.sesame" % "sesame-queryalgebra-model" % sesameVersion,
//    "org.openrdf.sesame" % "sesame-queryalgebra-evaluation" % sesameVersion,

    "org.apache.jena" % "apache-jena-libs" % jenaVersion ,//excludeAll(ExclusionRule(organization = "org.slf4j")),
    "ch.qos.logback" % "logback-classic" % "1.0.7" % "provided",
    "com.fasterxml" % "aalto-xml" % "0.9.7",
    "org.scalaz" %% "scalaz-core" % "7.0.4",
    "org.topbraid" % "spin" % "1.3.1"


  )


  lazy val semanticData = play.Project(semanticDataAppName, semanticDataAppVersion, semanticDataAppDependencies, path = file(this.semanticDataAppPath)).settings(
    // Add your own project settings here

    scalaVersion := scalaVer,
    resolvers += "Bigdata releases" at "http://systap.com/maven/releases/",
    resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/",
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    resolvers += "org.topbraid" at "http://topquadrant.com/repository/spin",
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

  ).dependsOn(macroses)
}

trait Macro{



  lazy val macroses = Project("macro", file("macro") ) settings(

    scalaVersion:=scalaVer,

    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _),

    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)

    //libraryDependencies += "org.scala-lang" % "scala-reflect" % scala


  )
}


object LibVersions {

  
  def src = "src"

  val jenaVersion = "2.11.1"

  val scalaGraphVersion = "1.7.2"

  val scalaVer = "2.10.3"

  val parboiledVersion = "2.0-M2"

  val bcryptVersion = "2.3"

 
  val scalaTimeVersion = "0.6.0"

  val apacheCommonsVersion = "1.3.2"

  val scalaCheckVersion = "1.11.0"

 //val bigDataVersion = "1.3.0" //BIGDATA doesnot support latest Sesame version

  val sesameVersion = "2.7.6"
  //val sesameVersion = "2.6.10" //BIGDATA doesnot support latest Sesame version

  val scalaTestVersion ="2.0"

  val bananaVersion ="0.5-SNAPSHOT"

  val bigDataVersion = "1.3.0"

  val playAuthVersion = "0.11.0"

  val nScalaTimeVersion = "0.8.0"

}


