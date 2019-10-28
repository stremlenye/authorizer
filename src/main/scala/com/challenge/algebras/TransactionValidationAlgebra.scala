package com.challenge.algebras

import com.challenge.models.{AccountInformation, Transaction}

trait TransactionValidationAlgebra[F[_]] {
  def validate(accountState: Option[AccountInformation], t: Transaction): F[AccountInformation]
}
