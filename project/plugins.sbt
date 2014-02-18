// Comment to get more information during initialization
logLevel := Level.Warn

addSbtPlugin("org.scala-lang.modules.scalajs" % "scalajs-sbt-plugin" % "0.3")

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.2-RC3")

addSbtPlugin("net.litola" % "play-sass" % "0.3.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-atmos-play" % "0.3.2")
