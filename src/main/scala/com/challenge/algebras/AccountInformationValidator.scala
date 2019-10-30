package com.challenge.algebras

import cats.data.ValidatedNel
import com.challenge.models.{AccountInformation, Violation}

trait AccountInformationValidator {
  def validate(i: Option[AccountInformation]): ValidatedNel[Violation, AccountInformation]
}
