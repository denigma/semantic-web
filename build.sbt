name                 := "semantic-web"

version              := "0.1.3"

Build.sameSettings

scalacOptions ++= Seq("-feature", "-language:_")


parallelExecution in Test := false

libraryDependencies += "org.denigma" %% "binding-play" % Versions.bindingPLay

libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1"

libraryDependencies += "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0"

libraryDependencies += "io.spray" % "spray-caching" % "1.3.1"

libraryDependencies +=  "org.webjars" %% "webjars-play" % "2.3.0-2"

libraryDependencies += "org.webjars" % "codemirror" % "4.5"

libraryDependencies += "org.webjars" % "d3js" % "3.4.11"

libraryDependencies += "org.webjars" % "jquery" % "2.1.1"

libraryDependencies += "org.webjars" % "jquery-ui" % "1.11.1"

libraryDependencies += "org.webjars" % "Semantic-UI" % "1.2.0"

libraryDependencies +="org.webjars" % "pdf-js" % "1.0.907"

libraryDependencies +="org.webjars" % "ckeditor" % "4.4.1"

libraryDependencies +="org.webjars" % "jquery-file-upload" % "9.5.7"

libraryDependencies += "org.webjars" % "selectize.js" % "0.11.2"

libraryDependencies += filters


// Apply RequireJS optimization, digest calculation and gzip compression to assets
pipelineStages := Seq(digest, gzip)
