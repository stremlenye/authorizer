package com.challenge.models

import cats.Eq
import cats.instances.string._
import cats.syntax.either._
import io.circe.{Decoder, Encoder}

sealed abstract class Violation(val code: String)

object Violation {
  implicit val encoder: Encoder[Violation] = Encoder.encodeString.contramap[Violation](_.code)
  implicit val decoder: Decoder[Violation] = Decoder.decodeString.emap {
    case DuplicatedAccount.code => DuplicatedAccount.asRight
    case AccountNotInitialised.code => AccountNotInitialised.asRight
    case CardNotActive.code => CardNotActive.asRight
    case InsufficientLimit.code => InsufficientLimit.asRight
    case HighFrequency.code => HighFrequency.asRight
    case DoubledTransaction.code => DoubledTransaction.asRight
    case ExceedLimitTopBoundary.code => ExceedLimitTopBoundary.asRight
    case other => s"'$other' is not a known violation type".asLeft
  }
  implicit val eq: Eq[Violation] = Eq.by[Violation, String](_.code)
}

case object DuplicatedAccount extends Violation("account-already-initialized")

case object AccountNotInitialised extends Violation("account-not-initialized")

case object CardNotActive extends Violation("card-not-active")

case object InsufficientLimit extends Violation("insufficient-limit")

case object HighFrequency extends Violation("high-frequency-small-interval")

case object DoubledTransaction extends Violation("doubled-transaction")

case object ExceedLimitTopBoundary extends Violation("exceed-limit-top-boundary")
