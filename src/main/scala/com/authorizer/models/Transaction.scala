package com.authorizer.models

import java.time.ZonedDateTime

import cats.Eq
import cats.instances.string._
import cats.instances.int._
import cats.instances.tuple._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

final case class Transaction(merchant: String, amount: Int, time: ZonedDateTime)

object Transaction {
  implicit val eq: Eq[Transaction] = Eq.by((a: Transaction) => (a.merchant, a.amount))

  implicit val decoder: Decoder[Transaction] = deriveDecoder
  implicit val encoder: Encoder[Transaction] = deriveEncoder
}
