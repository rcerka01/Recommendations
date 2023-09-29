package com.recommender.common

object errors {
  sealed trait AppError extends Throwable {
    def message: String
    override def getMessage: String = message
  }

  object AppError {
    final case class SourceRead(message: String) extends AppError
    final case class Internal(message: String) extends AppError
    final case class NotFound(message: String) extends AppError
  }
}

