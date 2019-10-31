package com.authorizer.algebras

import cats.data.ValidatedNel
import com.authorizer.models.{Account, AccountInformation, Violation}

trait AccountValidator {
  def validate(i: Option[AccountInformation], a: Account): ValidatedNel[Violation, Account]
}
