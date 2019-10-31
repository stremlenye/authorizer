package com.authorizer.algebras

import cats.data.ValidatedNel
import com.authorizer.models.{AccountInformation, Transaction, Violation}

trait TransactionValidator {
  def validate(i: AccountInformation, t: Transaction): ValidatedNel[Violation, Transaction]
}
