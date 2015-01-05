name := """shopservice"""

version := "0.1-SNAPSHOT"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  val slickV = "2.1.0"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-http"    % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.github.t3hnar"   %%  "scala-bcrypt"  % "2.4",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
    "com.typesafe.slick"  %%  "slick"         % slickV,
    "com.typesafe.slick"  %%  "slick-testkit" % slickV % "test",
    "org.postgresql"      %   "postgresql"    % "9.3-1102-jdbc4",
    "com.h2database"      %   "h2"            % "1.4.182",
    "com.jolbox"          %   "bonecp"        % "0.8.0.RELEASE",
    "ch.qos.logback"      %   "logback-classic" % "0.9.28"
  )
}

// testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

parallelExecution in Test := false

logBuffered := false

Revolver.settings
