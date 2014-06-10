Build.sameSettings

version := "0.3.3"

resolvers += "Bigdata releases" at "http://systap.com/maven/releases/"

resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/"

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "2.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.7"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" %  "2.3.3"

libraryDependencies += "com.bigdata" % "bigdata" % "1.3.1"

libraryDependencies += "org.openrdf.sesame" % "sesame-model" % "2.7.11"

libraryDependencies += "org.scalax" %% "semweb" % Build.semWebVersion


libraryDependencies += "org.scalax" %% "semweb-sesame" % Build.semWebVersion

libraryDependencies += "com.assembla.scala-incubator" %% "graph-core" % "1.9.0"

libraryDependencies += "com.assembla.scala-incubator" %% "graph-json" % "1.9.0"


