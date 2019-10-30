package com.challenge.algebras

import cats.data.ValidatedNel
import com.challenge.models.{AccountInformation, Transaction, Violation}

trait TransactionValidator {
  def validate(i: AccountInformation, t: Transaction): ValidatedNel[Violation, Transaction]
}
