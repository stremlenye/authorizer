package com.challenge.services.validators.accountinformation

import cats.data.{Validated, ValidatedNel}
import com.challenge.algebras.AccountInformationValidator
import com.challenge.models.{AccountInformation, AccountNotInitialised, Violation}

object AccountInformationExistenceValidator extends AccountInformationValidator {
  def validate(i : Option[AccountInformation]) : ValidatedNel[Violation, AccountInformation] =
    Validated.fromOption(i, AccountNotInitialised).toValidatedNel
}
