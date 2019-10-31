package com.authorizer.algebras

import cats.data.ValidatedNel
import com.authorizer.models.{AccountInformation, Violation}

trait AccountInformationValidator {
  def validate(i: Option[AccountInformation]): ValidatedNel[Violation, AccountInformation]
}
