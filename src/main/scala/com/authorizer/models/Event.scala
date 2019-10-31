package com.authorizer.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import io.circe.syntax._
import cats.syntax.either._

sealed trait Event

object Event {
  final case class CreateAccount(account: Account) extends Event

  object CreateAccount {
    implicit val decoder: Decoder[CreateAccount] = deriveDecoder
    implicit val encoder: Encoder[CreateAccount] = deriveEncoder
  }

  final case class CommitTransaction(transaction: Transaction) extends Event

  object CommitTransaction {
    implicit val decoder: Decoder[CommitTransaction] = deriveDecoder
    implicit val encoder: Encoder[CommitTransaction] = deriveEncoder
  }

  implicit val decoder: Decoder[Event] = Decoder.instance(h => h.as[CommitTransaction].orElse(h.as[Event.CreateAccount]))
  implicit val encoder: Encoder[Event] = Encoder.instance {
    case a: CreateAccount     => a.asJson
    case a: CommitTransaction => a.asJson
  }
}
