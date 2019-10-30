package com.challenge.models

import cats.data.NonEmptyList
import io.circe.Encoder
import io.circe.generic.semiauto._
import com.challenge.utils.codes._

sealed trait Result

object Result {
  final case class Success(account: Account) extends Result

  object Success {
    implicit val encoder: Encoder[Success] = deriveEncoder[Success]
  }

  final case class Failure(account: Option[Account], violations: NonEmptyList[Violation]) extends Result

  object Failure {
    implicit val encoder: Encoder[Failure] = deriveEncoder[Failure]
  }

  implicit val encoder: Encoder[Result] = deriveEncoder[Result]

  def success(account: Account): Result = Success(account)
  def failure(account: Option[Account], violations: NonEmptyList[Violation]): Result = Failure(account, violations)
}
