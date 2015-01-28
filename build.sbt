name                 := "semantic-web"

version              := "0.1.4"

Build.sameSettings

scalacOptions ++= Seq("-feature", "-language:_")

parallelExecution in Test := false

libraryDependencies ++= Dependencies.semanticWeb.value

libraryDependencies += filters


// Apply RequireJS optimization, digest calculation and gzip compression to assets
pipelineStages := Seq(digest, gzip)
