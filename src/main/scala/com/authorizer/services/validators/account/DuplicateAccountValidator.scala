package com.authorizer.services.validators.account

import cats.data.{Validated, ValidatedNel}
import com.authorizer.algebras.AccountValidator
import com.authorizer.models.{Account, AccountInformation, DuplicatedAccount, Violation}

object DuplicateAccountValidator extends AccountValidator {
  def validate(state : Option[AccountInformation], account : Account) : ValidatedNel[Violation, Account] =
    if(state.isDefined)
      Validated.invalidNel(DuplicatedAccount)
    else
      Validated.valid(account)
}
