package com.recommender.common.api

import cats.effect.Async
import com.comcast.ip4s.{Ipv4Address, Port}
import com.recommender.common.config.ServerConfig
import fs2.Stream
import fs2.io.net.Network
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder

import scala.concurrent.duration._

object Server {
  def serve[F[_]](config: ServerConfig, routes: HttpApp[F])(implicit F: Async[F]) =
      EmberServerBuilder
        .default(F, Network.forAsync[F])
        .withHostOption(Ipv4Address.fromString(config.host))
        .withPort(Port.fromInt(config.port).get)
        .withIdleTimeout(1.hour)
        .withHttpApp(routes)
        .build
        .use(_ => Async[F].never[Unit])
}
