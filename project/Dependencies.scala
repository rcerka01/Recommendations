import sbt._

object Dependencies {

  object Versions {
    val fs2        = "3.9.0"
    val pureConfig = "0.17.4"
    val circe      = "0.14.5"
    val sttp       = "3.8.16"
    val http4s     = "0.23.23"
    val logback    = "1.4.8"
    val log4cats   = "2.6.0"
    val tapir      = "1.6.4"

    val scalaTest = "3.2.16"
    val mockito   = "3.2.15.0"
  }

  object Libraries {

    object fs2 {
      val core = "co.fs2" %% "fs2-core" % Versions.fs2
      val io   = "co.fs2" %% "fs2-io"   % Versions.fs2

      val all = Seq(core, io)
    }

    object pureconfig {
      val core = "com.github.pureconfig" %% "pureconfig" % Versions.pureConfig
    }

    object logging {
      val logback  = "ch.qos.logback" % "logback-classic" % Versions.logback
      val log4cats = "org.typelevel" %% "log4cats-slf4j"  % Versions.log4cats

      val all = Seq(log4cats, logback)
    }

    object circe {
      val core    = "io.circe" %% "circe-core"    % Versions.circe
      val generic = "io.circe" %% "circe-generic" % Versions.circe
      val parser  = "io.circe" %% "circe-parser"  % Versions.circe

      val all = Seq(core, generic, parser)
    }

    object tapir {
      val core   = "com.softwaremill.sttp.tapir" %% "tapir-core"          % Versions.tapir
      val circe  = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % Versions.tapir
      val http4s = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % Versions.tapir

      val all = Seq(core, circe, http4s)
    }

    object http4s {
      val emberServer = "org.http4s" %% "http4s-ember-server" % Versions.http4s
    }

    val scalaTest = "org.scalatest"     %% "scalatest"   % Versions.scalaTest
    val mockito   = "org.scalatestplus" %% "mockito-4-6" % Versions.mockito
  }

  val core = Seq(
    Libraries.pureconfig.core,
    Libraries.http4s.emberServer
  ) ++
    Libraries.fs2.all ++
    Libraries.circe.all ++
    Libraries.tapir.all ++
    Libraries.logging.all

  val test = Seq(
    Libraries.scalaTest % Test,
    Libraries.mockito   % Test
  )

}
