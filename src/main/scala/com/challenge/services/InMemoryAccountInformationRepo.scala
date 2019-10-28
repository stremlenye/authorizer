package com.challenge.services

import cats.Monad
import cats.mtl.MonadState
import com.challenge.algebras.AccountInformationRepoAlgebra
import com.challenge.models.AccountInformation
import cats.syntax.functor._

class InMemoryAccountInformationRepo[F[_]](implicit F: MonadState[F, Option[AccountInformation]]) extends AccountInformationRepoAlgebra[F] {
  implicit protected val monad: Monad[F] = F.monad

  def set(a: AccountInformation): F[AccountInformation] =
    F.set(Some(a)).as(a)

  def get: F[Option[AccountInformation]] =
    F.get
}
