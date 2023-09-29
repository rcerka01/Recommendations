package com.recommender.engine

import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.recommender.common.api.Api
import org.typelevel.log4cats.Logger

final class Recommender[F[_]](
    val api: Api[F]
)

object Recommender {
  def make[F[_]](implicit F: Async[F], L: Logger[F]): F[Recommender[F]] =
    for {
      movieRepo <- MovieRepository.make[F]("metadatas.json")
      service <- RecommenderService.make[F](movieRepo)
      api <- RecommenderApi.make[F](service)
    } yield new Recommender[F](api)
}
