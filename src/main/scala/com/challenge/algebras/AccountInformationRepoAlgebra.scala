package com.challenge.algebras

import com.challenge.models.AccountInformation

trait AccountInformationRepoAlgebra[F[_]] {
  def set(a: AccountInformation): F[AccountInformation]
  def get: F[Option[AccountInformation]]
}
