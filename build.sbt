import sbt.Keys._
import sbt._

name := """ixias-play-auth-sample"""
organization := "com.github.taretmch"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.11"

libraryDependencies += guice

resolvers ++= Seq(
  "IxiaS Releases" at "http://maven.ixias.net.s3-ap-northeast-1.amazonaws.com/releases"
)

libraryDependencies ++= Seq(
  "net.ixias" %% "ixias"      % "1.1.28",
  "net.ixias" %% "ixias-aws"  % "1.1.28",
  "net.ixias" %% "ixias-play" % "1.1.28",
  "mysql" % "mysql-connector-java" % "5.1.+",
)
