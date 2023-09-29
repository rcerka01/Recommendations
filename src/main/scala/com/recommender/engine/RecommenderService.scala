package com.recommender.engine

import cats.MonadThrow
import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.recommender.common.errors.AppError

trait RecommenderService[F[_]] {
  def recommendMovie(id: String): F[Option[Movie]]
}

final private class LiveRecommenderService[F[_]](
    private val movieRepository: MovieRepository[F]
)(implicit
    F: MonadThrow[F]
) extends RecommenderService[F] {

  override def recommendMovie(id: String): F[Option[Movie]] =
    for {
      maybeMovie    <- movieRepository.find(id)
      movie         <- F.fromOption(maybeMovie, AppError.NotFound(s"Could not find movie with id $id"))
      similarMovies <- movieRepository.findByTags(movie.tags, 1, id)
    } yield similarMovies.headOption
}

object RecommenderService {
  def make[F[_]](movieRepository: MovieRepository[F])(implicit F: Async[F]): F[RecommenderService[F]] =
    F.pure(new LiveRecommenderService[F](movieRepository))
}
