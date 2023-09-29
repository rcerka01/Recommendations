package com.recommender.engine

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.recommender.common.errors.AppError
import org.mockito.ArgumentMatchers.{any, anyInt, anyString}
import org.mockito.Mockito
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.Future

class RecommenderServiceSpec extends AsyncWordSpec {

  "A recommender service" should {
    "correctly return recommended movie" in {
      val movie = Movie("1", "Test movie", 100, Set("test"))
      val recommendedMovie =
        Movie("2", "Test recommended movie", 100, Set("test"))

      val repository = Mockito.mock(classOf[MovieRepository[IO]])
      Mockito
        .when(repository.find(anyString()))
        .thenReturn(IO.pure(Some(movie)))
      Mockito
        .when(repository.findByTags(any[Set[String]](), anyInt(), anyString()))
        .thenReturn(IO.pure(List(recommendedMovie)))

      val service = RecommenderService.make[IO](repository)
      val resultIO = service.flatMap(_.recommendMovie("2")).attempt

      resultIO
        .unsafeToFuture()
        .map {
          case Left(error) =>
            fail(s"Expected a Movie but got an AppError: ${error.getMessage}")
          case Right(m) =>
            m shouldBe Some(recommendedMovie)
        }
        .flatMap { _ =>
          Future {
            Mockito.verify(repository).find("2")
            succeed
          }
        }
    }

    "return 404 when movie not found" in {
      val repository = Mockito.mock(classOf[MovieRepository[IO]])
      Mockito.when(repository.find(anyString())).thenReturn(IO.pure(None))

      val service = RecommenderService.make[IO](repository)
      val resultIO = service.flatMap(_.recommendMovie("1")).attempt

      resultIO
        .unsafeToFuture()
        .map {
          case Left(error: AppError) =>
            error.getMessage shouldBe "Could not find movie with id 1"
          case Left(otherError) =>
            fail(s"Unexpected error: ${otherError.getMessage}")
          case Right(_) =>
            fail("Expected an AppError but got a movie instead")
        }
        .flatMap { _ =>
          Future {
            Mockito.verify(repository).find("1")
            succeed
          }
        }
    }
  }
}
