package com.challenge.algebras

import cats.tagless.FunctorK
import com.challenge.models.AccountInformation
import cats.tagless.Derive

trait AccountInformationRepoAlgebra[F[_]] {
  def set(a: AccountInformation): F[AccountInformation]
  def get: F[Option[AccountInformation]]
}

object AccountInformationRepoAlgebra {
  implicit val functorK: FunctorK[AccountInformationRepoAlgebra] = Derive.functorK
}
