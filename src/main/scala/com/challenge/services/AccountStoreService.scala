package com.challenge.services

import cats.FlatMap
import com.challenge.algebras.{AccountAlgebra, AccountInformationRepoAlgebra, AccountValidationAlgebra, TransactionValidationAlgebra}
import com.challenge.models.{Account, Transaction}
import cats.syntax.functor._
import cats.syntax.flatMap._

class AccountStoreService[F[_]](
  repo: AccountInformationRepoAlgebra[F],
  accountValidator: AccountValidationAlgebra[F],
  transactionValidator: TransactionValidationAlgebra[F]
)(
  implicit F: FlatMap[F]
) extends AccountAlgebra[F] {

  def createAccount(a: Account): F[Account] =
    for {
      state <- repo.get
      valid <- accountValidator.validate(state, a)
      _ <- repo.set(valid.copy(account = a))
    } yield valid.account

  def commitTransaction(t: Transaction): F[Account] =
    for {
      state <- repo.get
      valid <- transactionValidator.validate(state, t)
      _ <- repo.set(
        valid.copy(
          transactions = t :: valid.transactions,
          account = valid.account.copy(availableLimit = valid.account.availableLimit + t.amount)
        )
      )
    } yield valid.account
}
