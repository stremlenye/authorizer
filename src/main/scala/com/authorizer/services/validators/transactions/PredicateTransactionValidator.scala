package com.authorizer.services.validators.transactions

import java.time.Duration

import cats.data.{Validated, ValidatedNel}
import com.authorizer.algebras.TransactionValidator
import com.authorizer.models.{
  AccountInformation,
  CardNotActive,
  DoubledTransaction,
  ExceedLimitTopBoundary,
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

object LimitValidator extends PredicateTransactionValidator(_.account.availableLimit.toLong + _.amount >= 0, InsufficientLimit)

class FrequencyValidator(interval: Duration, threshold: Int)
  extends PredicateTransactionValidator(
    (i, t) => i.transactions.sliding(t.time.minus(interval), t.time, interval).forall(_.size < threshold)
    ,
    HighFrequency
  )

class DoubledTransactionValidator(interval: Duration, threshold: Int)
  extends PredicateTransactionValidator(
    (i, t) => i.transactions.sliding(t.time.minus(interval), t.time, interval).forall(_.count(_ eqv t) < threshold),
    DoubledTransaction
  )

object LimitTopBoundaryValidator
  extends PredicateTransactionValidator(
    (i, t) => i.account.availableLimit.toLong + t.amount <= Int.MaxValue,
    ExceedLimitTopBoundary
  )
