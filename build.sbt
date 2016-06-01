name := "Account managment"

description := "Event source test"

val akkaVersion = "2.3.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)
