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
        "junit" % "junit" % "4.10",
        "org.hamcrest" % "hamcrest-library" % "1.3"
      ),
      resolvers ++= Seq(
        "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "releases"  at "http://oss.sonatype.org/content/repositories/releases"
      )
    )
  )
}
