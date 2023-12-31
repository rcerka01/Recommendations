package com.recommender

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import io.circe.parser.parse
import org.http4s.{Response, Status}
import org.scalatest.Assertion
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

trait ApiSpec extends AnyWordSpec with MockitoSugar with Matchers {

  def verifyJsonResponse(
      response: IO[Response[IO]],
      expectedStatus: Status,
      expectedBody: Option[String] = None
  ): Assertion =
    response
      .flatMap { res =>
        expectedBody match {
          case Some(expectedJson) =>
            res.as[String].map { receivedJson =>
              res.status mustBe expectedStatus
              parse(receivedJson) mustBe parse(expectedJson)
            }
          case None =>
            res.body.compile.toVector.map { receivedJson =>
              res.status mustBe expectedStatus
              receivedJson mustBe empty
            }
        }
      }
      .unsafeRunSync()(IORuntime.global)
}