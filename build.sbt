import sbt.Keys._
import sbt._

name := """ixias-play-auth-sample"""
organization := "sample.net.ixias"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice

resolvers ++= Seq(
  "IxiaS Releases" at "http://maven.ixias.net.s3-ap-northeast-1.amazonaws.com/releases"
)

libraryDependencies ++= Seq(
  "net.ixias" %% "ixias"      % "1.1.23",
  "net.ixias" %% "ixias-aws"  % "1.1.23",
  "net.ixias" %% "ixias-play" % "1.1.23",
  "mysql" % "mysql-connector-java" % "5.1.+",
)
