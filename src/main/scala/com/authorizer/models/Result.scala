package com.authorizer.models

import cats.data.NonEmptyList
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.semiauto._
import io.circe.syntax._
import cats.syntax.either._

sealed trait Result

object Result {
  final case class Success(account: Account) extends Result

  object Success {
    implicit val encoder: Encoder[Success] = deriveEncoder[Success].mapJson(_.mapObject(_.add("violations", Json.arr())))
    implicit val decoder: Decoder[Success] = deriveDecoder
  }

  final case class Failure(account: Option[Account], violations: NonEmptyList[Violation]) extends Result

  object Failure {
    implicit val encoder: Encoder[Failure] = deriveEncoder
    implicit val decoder: Decoder[Failure] = deriveDecoder
  }

  def success(account: Account): Result = Success(account)
  def failure(account: Option[Account], violations: NonEmptyList[Violation]): Result = Failure(account, violations)

  implicit val encoder: Encoder[Result] = Encoder.instance {
    case a: Success => a.asJson
    case a: Failure => a.asJson
  }

  implicit val decoder: Decoder[Result] = Decoder.instance { h =>
    h.as[Failure].orElse(h.as[Success])
  }
}
