package com.recommender.engine

import cats.effect.Async
import cats.syntax.either._
import cats.syntax.functor._
import cats.syntax.applicativeError._
import com.recommender.common.api.{Api, ErrorResponse}
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.server.http4s.Http4sServerInterpreter

final class RecommenderApi[F[_]](
    private val recommenderService: RecommenderService[F]
)(implicit
    F: Async[F]
) extends Api[F] {

  val recommendMovie = RecommenderApi.recommendMovie
    .serverLogic { movieId =>
      recommenderService
        .recommendMovie(movieId)
        .map(_.toRight(ErrorResponse.notFound(s"Could not find movie recommendation for movie $movieId")))
        .handleError(error => ErrorResponse.from(error).asLeft[Movie])
    }

  override def routes: HttpRoutes[F] =
    Http4sServerInterpreter[F]().toRoutes(List(recommendMovie))
}

object RecommenderApi extends TapirJsonCirce with SchemaDerivation {

  val basePath = "recommender"

  val recommendMovie = endpoint.post
    .in(basePath / "movies" / path[String])
    .out(jsonBody[Movie])
    .errorOut(Api.errorOutput)


  def make[F[_]](service: RecommenderService[F])(implicit F: Async[F]): F[Api[F]] =
    F.pure(new RecommenderApi[F](service))
}
