package com.challenge.algebras

import com.challenge.models.{Account, AccountInformation}

trait AccountValidationAlgebra[F[_]] {
  def validate(state: Option[AccountInformation], account: Account): F[AccountInformation]
}
