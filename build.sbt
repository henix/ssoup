name := "ssoup"

organization in Global := "henix"

version in Global := "0.1"

scalaVersion in Global := "2.11.4"

scalacOptions in Global ++= Seq("-deprecation", "-feature", "-Yno-adapted-args")

lazy val macros = project.in(file("macros"))

// http://grokbase.com/t/gg/simple-build-tool/133shekp07/sbt-avoid-dependence-in-a-macro-based-project

lazy val root = project.in(file(".")).dependsOn(macros % "compile-internal, test-internal").settings(
  mappings in (Compile, packageBin) ++= mappings.in(macros, Compile, packageBin).value,
  mappings in (Compile, packageSrc) ++= mappings.in(macros, Compile, packageSrc).value
)

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.1"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.2" % "test"
)
