package com.recommender.engine

import cats.effect.kernel.Async
import cats.implicits.catsSyntaxApplicativeId
import com.recommender.common.errors.AppError
import io.circe.parser._
import scala.io.Source
import io.circe.generic.auto._

case class MoviesWrapper(metadatas: List[Movie])

trait MovieRepository[F[_]] {
  def find(id: String): F[Option[Movie]]
  def findByTags(tags: Set[String], limit: Int, seedId: String): F[List[Movie]]
}

class MovieRepositoryLive[F[_]: Async](movies: Map[String, Movie]) extends MovieRepository[F] {

  override def find(id: String): F[Option[Movie]] = {
    movies.get(id).pure[F]
  }

  override def findByTags(tags: Set[String], limit: Int, seedId: String): F[List[Movie]] = {
    movies.values.toList
      .filter(_.id != seedId) // Exclude the seeded movie
      .map(movie => (movie, movie.tags.intersect(tags)))
      .filter(i => i._2.nonEmpty)
      .sortBy(-_._2.size)
      .take(limit)
      .map(_._1)
      .pure[F]
  }
}

object MovieRepository {
  private def loadMoviesFromResource(fileName: String): Either[Throwable, Map[String,Movie]] = {
    try {
      val content = {
        val source = Source.fromResource(fileName)
        try {
          source.mkString
        } finally {
          source.close()
        }
      }
      decode[MoviesWrapper](content).map(_.metadatas.map(movie => movie.id -> movie).toMap)
    } catch {
      case e: Exception => Left(e)
    }
  }

  def make[F[_]](fileName: String)(implicit F: Async[F]): F[MovieRepository[F]] = {
    loadMoviesFromResource(fileName) match {
      case Right(movies) => F.pure(new MovieRepositoryLive[F](movies))
      case Left(error) => F.raiseError(AppError.SourceRead(s"Error loading movies metadata: ${error.getMessage}"))
    }
  }
}
