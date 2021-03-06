organization in Global := "info.henix"

name := "ssoup"

description := "Scala CSS Selector DSL based on jsoup"

version in Global := "0.5"

licenses in Global := Seq("3-clause BSD" -> url("http://opensource.org/licenses/BSD-3-Clause"))

scalaVersion in Global := "2.12.4"

scalacOptions in Global ++= Seq("-deprecation", "-feature", "-Yno-adapted-args")

lazy val macros = project.in(file("macros")).settings(
  publish := {},
  publishLocal := {}
)

// http://grokbase.com/t/gg/simple-build-tool/133shekp07/sbt-avoid-dependence-in-a-macro-based-project

lazy val root = project.in(file(".")).dependsOn(macros % "compile-internal, test-internal").settings(
  mappings in (Compile, packageBin) ++= mappings.in(macros, Compile, packageBin).value,
  mappings in (Compile, packageSrc) ++= mappings.in(macros, Compile, packageSrc).value
)

lazy val jmh = project.in(file("jmh")).dependsOn(root)

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.11.2"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/henix/ssoup</url>
  <scm>
    <url>git@github.com:henix/ssoup.git</url>
    <connection>scm:git:git@github.com:henix/ssoup.git</connection>
  </scm>
  <developers>
    <developer>
      <id>henix</id>
      <name>henix</name>
      <url>https://github.com/henix</url>
    </developer>
  </developers>
)
