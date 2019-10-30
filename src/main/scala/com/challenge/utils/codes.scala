package com.challenge.utils

import cats.data.NonEmptyList
import io.circe.Encoder

object codes {
  implicit def nelEncoder[A : Encoder]: Encoder[NonEmptyList[A]] = Encoder.encodeList[A].contramap(_.toList)
}
