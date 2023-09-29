package com.recommender.engine

import io.circe._
import io.circe.generic.semiauto._

final case class Movie(
    id: String,
    title: String,
    length: Int,
    tags: Set[String]
)

object Movie {
  implicit val decoder: Decoder[Movie] = deriveDecoder[Movie]
  implicit val encoder: Encoder[Movie] = deriveEncoder[Movie]
}
