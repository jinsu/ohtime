name := "soju_time"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.google.inject" % "guice" % "4.0-beta",
  "org.postgresql" % "postgresql" % "9.3-1101-jdbc4",
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "holderjs" % "2.3.0",
  "joda-time" % "joda-time" % "2.3"
)

play.Project.playScalaSettings

templatesImport += "services._"

routesImport += "binders._"
