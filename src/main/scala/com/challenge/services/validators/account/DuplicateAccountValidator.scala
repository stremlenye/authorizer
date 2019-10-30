package com.challenge.services.validators.account

import cats.data.{Validated, ValidatedNel}
import com.challenge.algebras.NewAccountValidator
import com.challenge.models.{Account, AccountInformation, DuplicatedAccount, Violation}

object DuplicateAccountValidator extends NewAccountValidator {
  def validate(state : Option[AccountInformation], account : Account) : ValidatedNel[Violation, Account] =
    if(state.isDefined)
      Validated.invalidNel(DuplicatedAccount)
    else
      Validated.valid(account)
}
