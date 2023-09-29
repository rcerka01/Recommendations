package com.recommender.common

import cats.effect.Sync
import pureconfig._
import pureconfig.generic.auto._

object config {

  final case class ServerConfig(
      host: String,
      port: Int
  )

  final case class AppConfig(
      server: ServerConfig
  )

  object AppConfig {
    def load[F[_]](implicit F: Sync[F]): F[AppConfig] =
      F.blocking(ConfigSource.default.loadOrThrow[AppConfig])
  }
}
