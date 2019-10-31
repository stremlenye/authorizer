package com.authorizer.algebras

import cats.tagless.FunctorK
import com.authorizer.models.AccountInformation
import cats.tagless.Derive

trait AccountInformationRepoAlgebra[F[_]] {
  def set(a: AccountInformation): F[AccountInformation]
  def get: F[Option[AccountInformation]]
}

object AccountInformationRepoAlgebra {
  implicit val functorK: FunctorK[AccountInformationRepoAlgebra] = Derive.functorK
}
