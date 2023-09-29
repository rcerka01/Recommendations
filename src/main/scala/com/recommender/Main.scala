package com.recommender

import cats.effect.{IO, IOApp}
import com.recommender.common.api.Server
import com.recommender.common.config.AppConfig
import com.recommender.engine.Recommender
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]
  override val run: IO[Unit] =
    for {
      config      <- AppConfig.load[IO]
      recommender <- Recommender.make[IO]
      routes = recommender.api.routes.orNotFound
      _ <- Server.serve[IO](config.server, routes)
    } yield ()
}
