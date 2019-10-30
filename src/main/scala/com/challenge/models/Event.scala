package com.challenge.models

import io.circe.Decoder
import io.circe.generic.semiauto._

sealed trait Event

object Event {
  final case class CreateAccount(account: Account) extends Event

  object CreateAccount {
    implicit val decoder: Decoder[CreateAccount] = deriveDecoder
  }

  final case class CommitTransaction(transaction: Transaction) extends Event

  object CommitTransaction {
    implicit val decoder: Decoder[CommitTransaction] = deriveDecoder
  }

  implicit val decoder: Decoder[Event] = deriveDecoder[Event]
}
