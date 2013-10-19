import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "CommandControlCentre"
  val appVersion = "1.0-SNAPSHOT"

  //resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

  val appDependencies = Seq(
    javaCore, javaJdbc, jdbc, anorm, javaEbean, cache,
    //"mysql" % "mysql-connector-java" % "5.1.21"
    "postgresql" % "postgresql" % "9.1-901.jdbc4" withSources,
    //"com.typesafe.slick" % "slick_2.10.1" % "2.0.0-M1",
    "com.typesafe.slick" % "slick_2.10" % "1.0.1" withSources,
    "org.slf4j" % "slf4j-nop" % "1.6.4" withSources,
    "com.github.nscala-time" %% "nscala-time" % "0.6.0" withSources,
    "com.github.tototoshi" %% "slick-joda-mapper" % "0.4.0" withSources,
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0-SNAPSHOT" exclude("org.scala-stm", "scala-stm_2.10.0") exclude("play", "*") withSources
    )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    templatesImport ++= Seq("views.utils.Helper._", "patterns.models.Persisted"),
    scalacOptions ++= Seq("-deprecation", "-feature"))
}
 