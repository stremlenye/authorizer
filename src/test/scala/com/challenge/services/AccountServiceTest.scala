package com.challenge.services

import cats.syntax.eq._
import com.challenge.{Main, StoreContext}
import com.challenge.algebras.AccountAlgebra
import com.challenge.models._
import com.challenge.test.util.ResultAssertions
import com.challenge.test.util.syntax._
import com.challenge.test.util.Fixtures._
import org.scalatest.FlatSpec

class AccountServiceTest extends FlatSpec with ResultAssertions {

  val service: AccountAlgebra[StoreContext] = Main.service

  it should "create an account while in initial state" in {
    val payload = Account(activeCard = false, availableLimit = 0)
    service.createAccount(payload).map(expectSuccess(_)(a => assert(a eqv payload))).withState(state.empty)
  }

  it should "fail to create duplicate account" in {
    val payload = Account(activeCard = false, availableLimit = 0)
    (for {
      _ <- service.createAccount(payload)
      result <- service.createAccount(payload)
    } yield result).map(expectViolations(_)(DuplicatedAccount)).withState(state.empty)
  }

  it should "not accept transaction if account was not created prior" in {
    service.commitTransaction(transaction.merchantOneM100).map(expectViolations(_)(AccountNotInitialised))
  }.withState(state.empty)

  it should "not accept transaction if account's card is not active" in {
    service.commitTransaction(transaction.merchantOneM100).map(expectViolations(_)(CardNotActive)).withState(state.inactiveCard1KLimit)
  }

  it should "not accept transaction if account has not enough funds" in {
    service.commitTransaction(transaction.merchantOneM100).map(expectViolations(_)(InsufficientLimit)).withState(state.activeCardZeroLimit)
  }

  it should "not accept transaction if 3 transactions were accepted on a 2 min interval" in {
    for {
      _ <- service.commitTransaction(transaction.merchantOneM100.earlierByMinutes(2)).map(expectSuccess)
      _ <- service.commitTransaction(transaction.merchantTwoM100.earlierByMinutes(1)).map(expectSuccess)
      _ <- service.commitTransaction(transaction.merchantThreeM100.earlierByMinutes(1)).map(expectSuccess)
      result <- service.commitTransaction(transaction.merchantFourM100).map(expectViolations(_)(HighFrequency))
    } yield result
  }.withState(state.activeCard1KLimit)

  it should "not accept transaction if 2 transactions from same merchant were accepted on a 2 min interval" in {
    for {
      _ <- service.commitTransaction(transaction.merchantOneM100.earlierByMinutes(2)).map(expectSuccess)
      _ <- service.commitTransaction(transaction.merchantOneM100.earlierByMinutes(1)).map(expectSuccess)
      result <- service.commitTransaction(transaction.merchantOneM100).map(expectViolations(_)(DoubledTransaction))
    } yield result
  }.withState(state.activeCard1KLimit)

  it should "not accept transaction if available limit exceeds the type boundaries " in {
    service.commitTransaction(transaction.merchantOneP100).map(expectViolations(_)(ExceedLimitTopBoundary))
  }.withState(state.activeCardMaxLimit)

  it should "accept transaction if no rules were violated" in {
    val t = transaction.merchantOneM100
    val acc = account.activeCard1KLimit
    service
      .commitTransaction(t).map(
        expectSuccess(_)(a => assert(acc.availableLimit + t.amount == a.availableLimit))
      ).withState(state.from(acc))
  }
}
