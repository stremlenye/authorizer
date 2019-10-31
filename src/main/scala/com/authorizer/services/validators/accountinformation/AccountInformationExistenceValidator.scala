package com.authorizer.services.validators.accountinformation

import cats.data.{Validated, ValidatedNel}
import com.authorizer.algebras.AccountInformationValidator
import com.authorizer.models.{AccountInformation, AccountNotInitialised, Violation}

object AccountInformationExistenceValidator extends AccountInformationValidator {
  def validate(i : Option[AccountInformation]) : ValidatedNel[Violation, AccountInformation] =
    Validated.fromOption(i, AccountNotInitialised).toValidatedNel
}
