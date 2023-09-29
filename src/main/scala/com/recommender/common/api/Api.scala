package com.recommender.common.api

import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{oneOf, oneOfDefaultVariant, oneOfVariant}

trait Api[F[_]] {
  def routes: HttpRoutes[F]
}

object Api extends TapirJsonCirce with SchemaDerivation {
  val errorOutput = oneOf[ErrorResponse](
    oneOfVariant(StatusCode.NotFound, jsonBody[ErrorResponse.NotFound]),
    oneOfDefaultVariant(jsonBody[ErrorResponse.Internal])
  )
}
