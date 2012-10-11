import sbt._
import sbt.Keys._

object GoosscalaBuild extends Build {

  lazy val goosscala = Project(
    id = "goos-scala",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "goos-scala",
      organization := "auctionsniper",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2" % "1.12.1" % "test",
        "org.hamcrest" % "hamcrest-library" % "1.3",
        "jivesoftware" % "smack" % "3.1.0",
        "jivesoftware" % "smackx" % "3.1.0",
        "org.mockito" % "mockito-all" % "1.9.0",
        "org.apache.commons" % "commons-lang3" % "3.1",
        "commons-io" % "commons-io" % "2.4"
      ),
      resolvers ++= Seq(
        "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "releases"  at "http://oss.sonatype.org/content/repositories/releases"
      )
    )
  )
}
