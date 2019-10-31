package com.challenge

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime

import cats.data.NonEmptyList
import com.challenge.models.Event.{CommitTransaction, CreateAccount}
import com.challenge.models._
import io.circe.syntax._
import io.circe.parser._
import org.scalatest.FlatSpec
import cats.syntax.traverse._
import cats.syntax.foldable._
import cats.syntax.bifunctor._
import cats.instances.list._
import cats.instances.either._
import com.challenge.models.Result.{Failure, Success}

class MainTest extends FlatSpec {

  def runWithInput(events: List[Event]): Either[NonEmptyList[io.circe.Error], List[Result]] = {
    val input = events.map(_.asJson.noSpaces).mkString("\n")
    val inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    val outputStream = new ByteArrayOutputStream()
    Console.withIn(inputStream) {
      Console.withOut(outputStream) {
        Main.run(List.empty).unsafeRunSync()
      }
    }
    new String(outputStream.toByteArray, StandardCharsets.UTF_8)
      .split('\n').toList.map(decode[Result])
      .flatTraverse[λ[a => Either[NonEmptyList[io.circe.Error], a]], Result](_.bimap(NonEmptyList.one, a => List.apply(a)))
  }

  it should "give correct response on correct input" in {

    val timestamp = Stream.iterate(ZonedDateTime.now().minusHours(1))(_.plusSeconds(30)).iterator
    val transaction = CommitTransaction(Transaction("merchant1", 10, timestamp.next()))
    val transactions = List(
      Transaction("merchant1", 10, timestamp.next()), // success
      Transaction("merchant1", -10, timestamp.next()), // success
      Transaction("merchant1", -10, timestamp.next()), // fail: doubled-transaction
      Transaction("merchant2", 10, timestamp.next()), // success
      Transaction("merchant2", -10, timestamp.next()), // fail: high-frequency-small-interval
      Transaction("merchant2", -10, timestamp.next().plusMinutes(1)), // success
      Transaction("merchant2", -1000, timestamp.next().minusHours(1)), // success
      Transaction("merchant2", -1, timestamp.next()) // fail: insufficient-limit
    ).map(CommitTransaction.apply)

    val account = CreateAccount(Account(activeCard = true, availableLimit = 1000))
    val input: List[Event] =
      transaction :: account :: account :: transactions

    val expected = List(
      Failure(None, NonEmptyList.one(AccountNotInitialised)),
      Success(Account(activeCard = true, availableLimit = 1000)),
      Failure(Some(Account(activeCard = true, availableLimit = 1000)), NonEmptyList.one(DuplicatedAccount)),
      Success(Account(activeCard = true, availableLimit = 1010)),
      Success(Account(activeCard = true, availableLimit = 1000)),
      Failure(Some(Account(activeCard = true, availableLimit = 1000)), NonEmptyList.one(DoubledTransaction)),
      Success(Account(activeCard = true, availableLimit = 1010)),
      Failure(Some(Account(activeCard = true, availableLimit = 1010)), NonEmptyList.one(HighFrequency)),
      Success(Account(activeCard = true, availableLimit = 1000)),
      Success(Account(activeCard = true, availableLimit = 0)),
      Failure(Some(Account(activeCard = true, availableLimit = 0)), NonEmptyList.one(InsufficientLimit))
    )

    runWithInput(input) match {
      case Left(errors) => fail(errors.head)
      case Right(results) =>
        assert(
          results == expected
        )
    }
  }

  it should "give correct response on correct input (inactive card)" in {
    val transaction = CommitTransaction(Transaction("merchant1", -10, ZonedDateTime.now))
    val account = CreateAccount(Account(activeCard = false, availableLimit = 0))
    val input: List[Event] =
      List(account, transaction)

    val expected = List(
      Success(account.account),
      Failure(Some(account.account), NonEmptyList.of(CardNotActive, InsufficientLimit))
    )

    runWithInput(input) match {
      case Left(errors) => fail(errors.head)
      case Right(results) =>
        assert(
          results == expected
        )
    }
  }
}
