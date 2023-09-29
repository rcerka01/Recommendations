package com.recommender.engine

import cats.effect.IO
import com.recommender.ApiSpec
import org.mockito.Mockito
import org.http4s.implicits._
import org.http4s._
import org.mockito.ArgumentMatchers.anyString

class RecommenderApiSpec extends ApiSpec {

  val movie = Movie("1", "Test movie", 100, Set("test"))

  "A RecommenderApi" when  {
    "POST /recommender/movies/:id" should {
      "return 200 on success" in {
        val service = Mockito.mock(classOf[RecommenderService[IO]])
        Mockito.when(service.recommendMovie(anyString())).thenReturn(IO.pure(Some(movie)))

        val api = new RecommenderApi[IO](service)

        val request = Request[IO](uri = uri"/recommender/movies/1", method = Method.POST)
        val response = api.routes.orNotFound.run(request)

        val expectedResponseBody = """{
                                     |  "id" : "1",
                                     |  "title" : "Test movie",
                                     |  "length" : 100,
                                     |  "tags" : [
                                     |    "test"
                                     |  ]
                                     |}""".stripMargin

        verifyJsonResponse(response, Status.Ok, Some(expectedResponseBody))
        Mockito.verify(service).recommendMovie("1")
      }

      "return 404 when recommendation not found" in {
        val service = Mockito.mock(classOf[RecommenderService[IO]])
        Mockito.when(service.recommendMovie(anyString())).thenReturn(IO.pure(None))

        val api = new RecommenderApi[IO](service)

        val request = Request[IO](uri = uri"/recommender/movies/1", method = Method.POST)
        val response = api.routes.orNotFound.run(request)

        verifyJsonResponse(response, Status.NotFound, Some("""{"message":"Could not find movie recommendation for movie 1"}"""))
        Mockito.verify(service).recommendMovie("1")
      }
    }
  }
}
