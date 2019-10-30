package com.challenge.services

import cats.Monad
import com.challenge.algebras.{AccountAlgebra, AccountInformationRepoAlgebra, AccountInformationValidator, NewAccountValidator, TransactionValidator}
import com.challenge.models._
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.applicative._

class AccountService[F[_]](
                            repo: AccountInformationRepoAlgebra[F],
                            newAccountValidator: NewAccountValidator,
                            accountInformationValidator: AccountInformationValidator,
                            transactionValidator: TransactionValidator
)(
  implicit F: Monad[F]
) extends AccountAlgebra[F] {

  def createAccount(a: Account): F[Result] =
    for {
      state <- repo.get
      validated = newAccountValidator.validate(state, a)
      result <- validated.fold[F[Result]](
        Result.failure(None, _).pure[F],
        account => repo.set(AccountInformation(account)).as(Result.success(account))
      )
    } yield result

  def commitTransaction(transaction: Transaction): F[Result] =
    for {
      state <- repo.get
      accountInformation = accountInformationValidator.validate(state)
      result <- accountInformation.fold(
        Result.failure(None, _).pure[F],
        accountInformation => transactionValidator.validate(accountInformation, transaction).fold(
          Result.failure(Some(accountInformation.account), _).pure[F],
          t => repo.set(accountInformation.submitTransaction(t)).as(Result.success(accountInformation.account))
        )
      )
    } yield result
}
