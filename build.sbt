name := "ssoup"

organization in Global := "henix"

version in Global := "0.1"

scalaVersion in Global := "2.11.4"

scalacOptions in Global ++= Seq("-deprecation", "-feature", "-Yno-adapted-args")

lazy val macros = project.in(file("macros"))

lazy val root = project.in(file(".")).dependsOn(macros)

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.1"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.2" % "test"
)
