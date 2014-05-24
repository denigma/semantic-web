name                 := "semantic-web"

version              := "0.05"

organization := "org.denigma"

scalacOptions ++= Seq("-feature", "-language:_")

parallelExecution in Test := false

libraryDependencies += filters

libraryDependencies +="org.scalajs" %% "scalajs-pickling" % "0.2"

// Apply RequireJS optimization, digest calculation and gzip compression to assets
pipelineStages := Seq(digest, gzip)
