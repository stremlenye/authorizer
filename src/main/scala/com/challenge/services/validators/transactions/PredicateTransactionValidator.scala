package com.challenge.services.validators.transactions

import java.time.Duration

import cats.data.{Validated, ValidatedNel}
import com.challenge.algebras.TransactionValidator
import com.challenge.models.{
  AccountInformation,
  CardNotActive,
  DoubledTransaction,
  HighFrequency,
  InsufficientLimit,
  Transaction,
  Violation
}
import cats.syntax.eq._

sealed abstract class PredicateTransactionValidator(p: (AccountInformation, Transaction) => Boolean, violation: Violation)
  extends TransactionValidator {
  override def validate(i: AccountInformation, t: Transaction): ValidatedNel[Violation, Transaction] =
    if (p(i, t)) Validated.validNel(t) else Validated.invalidNel(violation)
}

object ActiveCardValidator extends PredicateTransactionValidator((i, _) => i.account.activeCard, CardNotActive)

object LimitValidator extends PredicateTransactionValidator(_.account.availableLimit + _.amount >= 0, InsufficientLimit)

class FrequencyValidator(interval: Duration, threshold: Int)
  extends PredicateTransactionValidator(
    (i, t) =>
      List(
        i.transactions.getInInterval(t.time, t.time.plus(interval)).length,
        i.transactions.getInInterval(t.time.minus(interval), t.time).length
      ).exists(_ > threshold),
    HighFrequency
  )

class DoubledTransactionValidator(interval: Duration, threshold: Int)
  extends PredicateTransactionValidator(
    (i, t) =>
      List(
        i.transactions.getInInterval(t.time, t.time.plus(interval)).filter(_ eqv t),
        i.transactions.getInInterval(t.time.minus(interval), t.time)
      ).map(_.count(_ eqv t)).exists(_ > threshold),
    DoubledTransaction
  )
