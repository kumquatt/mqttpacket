name := "mqttpacket"

version := "1.0"

scalaVersion := "2.11.6"

resolvers ++= {
  Seq(
    "repo" at "http://repo.typesafe.com/typesafe/releases/",
    "Paho MQTT Client" at "https://repo.eclipse.org/content/repositories/paho-releases/"
  )
}

libraryDependencies ++= {
  Seq(
    "junit" % "junit" % "4.10",
    "org.scodec" %% "scodec-core" % "1.7.1",
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
  )
}