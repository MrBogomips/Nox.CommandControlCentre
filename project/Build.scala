import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "CommandControlCentre"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      javaCore, javaJdbc, jdbc, anorm, javaEbean,
      //"mysql" % "mysql-connector-java" % "5.1.21"
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "com.typesafe.slick" %% "slick" % "1.0.0",
      "org.slf4j" % "slf4j-nop" % "1.6.4"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
      templatesImport += "views.utils.Helper._"
    )

}
