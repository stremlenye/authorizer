package com.authorizer.algebras

import cats.data.ValidatedNel
import com.authorizer.models.{Account, AccountInformation, Violation}

trait NewAccountValidator {
  def validate(i: Option[AccountInformation], a: Account): ValidatedNel[Violation, Account]
}
