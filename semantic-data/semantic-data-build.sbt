Build.sameSettings

version := "0.3.4"

resolvers += "Bigdata releases" at "http://systap.com/maven/releases/"

resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/"

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "spray repo" at "http://repo.spray.io"


libraryDependencies ++= Dependencies.semanticData.value

