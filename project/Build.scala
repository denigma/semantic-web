import sbt._

import sbt.Keys._

import play._

import play.Play.autoImport._

import PlayKeys._

import scala.scalajs.sbtplugin.ScalaJSPlugin._

import ScalaJSKeys._

import com.typesafe.sbt.packager.universal.UniversalKeys

import bintray.Keys._


/**
 * this files is intended to build the main project
 * it contains links to all dependencies that are needed
 * */
object ApplicationBuild extends Build with SemanticWeb



trait SemanticWeb extends SemanticData with ScalaJS with UniversalKeys{

  override def semanticDataAppPath = "./semantic-data"



  //  val testOptions = "-Dconfig.file=conf/" + Option(System.getProperty("test.config")).getOrElse("application") + ".conf"


  val appName         = "semantic-web"
  val appVersion      = "0.05"


  val appDependencies: Seq[ModuleID] =
    Dependencies.authDepth++Dependencies.webjars++Dependencies.diDeps++Dependencies.playModules++
      Seq(
        filters,
        "org.scalajs" %% "scalajs-pickling" % "0.2",
        "org.scalajs" %% "scalajs-pickling-play-json" % "0.2"
      )

  lazy val sharedModels = unmanagedSourceDirectories in Compile += baseDirectory.value / "models" / "src" / "main" / "scala"


  lazy val semanticWebSettings = Seq(



      //resolvers += Resolver.file("Local repo", file("~/.ivy2/local"))(Resolver.ivyStylePatterns) ,

      sharedModels,

      ScalaJSKeys.relativeSourceMaps := true, //just in case as sourcemaps do not seem to work=(

      scalaVersion:=Dependencies.scalaVer,

      resolvers +=   Dependencies.scalajsResolver,

      //scalajsOutputDir     := (crossTarget in Compile).value / "classes" / "public" / "javascripts",

      scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      compile in Compile <<= (compile in Compile) dependsOn (preoptimizeJS in (scalajs, Compile)),

      test in Test <<= (test in Test) dependsOn (test in (semanticData, Test)),

      //for test only
      //compile in Compile <<= (compile in Compile) dependsOn (packageJS in (scalajs, Compile)),

      dist <<= dist dependsOn (optimizeJS in (scalajs, Compile)),

      watchSources <++= (sourceDirectory in (scalajs, Compile)).map { path => (path ** "*.scala").get},


      libraryDependencies ++=appDependencies
      //incOptions := incOptions.value.withNameHashing(true)

    ) ++ (     // ask scalajs project to put its outputs in scalajsOutputDir
       Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, preoptimizeJS, optimizeJS) map {
        packageJSKey => crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      } )  ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++  publishSettings




  //play.Project.playScalaSettings ++ SassPlugin.sassSettings

  //val main = play.Project(appName, appVersion, appDependencies).settings(semanticWebSettings: _*).dependsOn(semanticData).aggregate(scalajs)


  lazy val main = (project in file(".")).enablePlugins(PlayScala).settings(semanticWebSettings: _*).dependsOn(semanticData).aggregate(scalajs)



  //SassPlugin.sassSettings ++
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass")):_* )
  //Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass","-r", "semantic-ui-sass", "-r","singularitygs")):_* )
}





