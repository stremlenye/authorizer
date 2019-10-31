package com.authorizer.services.validators.account

import cats.data.{Validated, ValidatedNel}
import com.authorizer.algebras.NewAccountValidator
import com.authorizer.models.{Account, AccountInformation, DuplicatedAccount, Violation}

object DuplicateAccountValidator extends NewAccountValidator {
  def validate(state : Option[AccountInformation], account : Account) : ValidatedNel[Violation, Account] =
    if(state.isDefined)
      Validated.invalidNel(DuplicatedAccount)
    else
      Validated.valid(account)
}
