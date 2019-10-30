package com.challenge.algebras

import cats.data.ValidatedNel
import com.challenge.models.{Account, AccountInformation, Violation}

trait NewAccountValidator {
  def validate(i: Option[AccountInformation], a: Account): ValidatedNel[Violation, Account]
}
