import bintray.Keys._
import sbt._
import sbt.Keys._
import scala.Some

trait Publish {

  protected val bintrayPublishIvyStyle =
    settingKey[Boolean]("=== !publishMavenStyle")

  lazy val publishSettings = Seq(
    repository in bintray := "denigma-releases",
    bintrayOrganization in bintray := Some("denigma"),
    Def.derive(bintrayPublishIvyStyle := !publishMavenStyle.value)
  )

  /**
   * For parts of the project that we will not publish
   */
  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )
}