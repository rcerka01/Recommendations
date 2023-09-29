package com.recommender.common.api

import com.recommender.common.errors.AppError
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

sealed trait ErrorResponse {
  def message: String
}

object ErrorResponse {

  def from(error: Throwable): ErrorResponse =
    error match {
      case AppError.NotFound(m) => NotFound(m)
      case error                => Internal(error.getMessage)
    }

  def notFound(message: String): ErrorResponse =
    NotFound(message)

  final case class NotFound(message: String) extends ErrorResponse
  object NotFound {
    implicit val decoder: Decoder[NotFound] = deriveDecoder[NotFound]
    implicit val encoder: Encoder[NotFound] = deriveEncoder[NotFound]
  }
  final case class Internal(message: String) extends ErrorResponse
  object Internal {
    implicit val decoder: Decoder[Internal] = deriveDecoder[Internal]
    implicit val encoder: Encoder[Internal] = deriveEncoder[Internal]
  }

  implicit val encoder: Encoder[ErrorResponse] = Encoder.instance {
    case e: Internal => e.asJson
    case e: NotFound => e.asJson
  }
}
