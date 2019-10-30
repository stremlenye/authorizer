package com.challenge.models

import cats.Eq
import io.circe.Encoder
import cats.instances.string._

sealed abstract class Violation(val code: String)

object Violation {
  implicit val encoder: Encoder[Violation] = Encoder.encodeString.contramap[Violation](_.code)
  implicit val eq: Eq[Violation] = Eq.by[Violation, String](_.code)
}

case object DuplicatedAccount extends Violation("account-already-initialized")

case object AccountNotInitialised extends Violation("account-not-initialized")

case object CardNotActive extends Violation("card-not-active")

case object InsufficientLimit extends Violation("insufficient-limit")

case object HighFrequency extends Violation("high-frequency-small-interval")

case object DoubledTransaction extends Violation("doubled-transaction")

case object ExceedLimitTopBoundary extends Violation("exceed-limit-top-boundary")
