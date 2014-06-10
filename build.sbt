name                 := "semantic-web"

version              := "0.06"

Build.sameSettings

scalacOptions ++= Seq("-feature", "-language:_")

ScalaJSKeys.relativeSourceMaps := true //just in case as sourcemaps do not seem to work=(

parallelExecution in Test := false

// includeFilter in (Assets, LessKeys.less) := "*.less"

// excludeFilter in (Assets, LessKeys.less) := "_*.less"

libraryDependencies += "org.denigma" %% "binding-play" % Build.bindingVersion

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1"

libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3"

libraryDependencies += "com.typesafe" %% "play-plugins-mailer" % "2.2.0" //mailer for email confirmations

libraryDependencies += "io.spray" % "spray-caching" % "1.3.1"

libraryDependencies +=  "org.webjars" %% "webjars-play" % "2.3.0"

libraryDependencies += "org.webjars" % "codemirror" % "4.1"

libraryDependencies += "org.webjars" % "d3js" % "3.4.6"

libraryDependencies += "org.webjars" % "d3-plugins" % "da342b6"

libraryDependencies += "org.webjars" % "jquery" % "2.1.1"

libraryDependencies += "org.webjars" % "jquery-ui" % "1.10.4"

libraryDependencies += "org.webjars" % "Semantic-UI" % "0.17.0"

libraryDependencies +="org.webjars" % "pdf-js" % "0.8.1170"

libraryDependencies +="org.webjars" % "requirejs" % "2.1.11-1"

libraryDependencies +="org.webjars" % "ckeditor" % "4.3.4"

libraryDependencies += filters


// Apply RequireJS optimization, digest calculation and gzip compression to assets
pipelineStages := Seq(digest, gzip)
