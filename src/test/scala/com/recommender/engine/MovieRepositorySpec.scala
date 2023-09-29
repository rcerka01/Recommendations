package com.recommender.engine

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.recommender.common.errors.AppError
import org.scalatest.wordspec.AsyncWordSpec

class MovieRepositorySpec extends AsyncWordSpec {
  val repository: IO[MovieRepository[IO]] =
    MovieRepository.make[IO]("testMetadata.json")

  "A Movie repository" should {
    "return movie by id" in {
      val expected = Movie("1", "title1", 100, Set("Crime", "Drama", "Prison"))
      (for {
        r <- repository
        movieOpt <- r.find("1")
      } yield {
        assert(movieOpt.contains(expected))
      }).unsafeToFuture()
    }

    "return none for non existing movie" in {
      (for {
        r <- repository
        movieOpt <- r.find("9")
      } yield {
        assert(movieOpt.isEmpty)
      }).unsafeToFuture()
    }

    "find by most tags matched" in {
      val expected = Movie(
        "4",
        "title4",
        100,
        Set("Crime", "Prison", "Crime Family", "Mafia")
      )
      (for {
        r <- repository
        movieOpt <- r.findByTags(Set("Crime", "Prison", "Mafia"), 1, "1")
      } yield {
        assert(movieOpt.contains(expected))
      }).unsafeToFuture()
    }

    "return empty list for no matched tags" in {
      (for {
        r <- repository
        movieOpt <- r.findByTags(Set("Nonexistent"), 1, "1")
      } yield {
        assert(movieOpt.isEmpty)
      }).unsafeToFuture()
    }

    "return valid exception for wrong source" in {
      val repository: IO[MovieRepository[IO]] =
        MovieRepository.make[IO]("invalid-file-name")

      val thrown = intercept[AppError] {
        repository.unsafeRunSync()
      }
      assert(
        thrown.getMessage == "Error loading movies metadata: resource 'invalid-file-name' was not found in the classpath from the given classloader."
      )
    }
  }
}
