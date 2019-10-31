package com.authorizer

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime

import cats.data.NonEmptyList
import com.authorizer.models.Event.{CommitTransaction, CreateAccount}
import com.authorizer.models._
import io.circe.Error
import io.circe.syntax._
import io.circe.parser._
import org.scalatest.FlatSpec
import cats.syntax.traverse._
import cats.syntax.foldable._
import cats.syntax.bifunctor._
import cats.instances.list._
import cats.instances.either._
import com.authorizer.models.Result.{Failure, Success}

class MainTest extends FlatSpec {

  def runWithEvents(events: List[Event]): Either[NonEmptyList[Error], List[Result]] = {
    val input = events.map(_.asJson.noSpaces).mkString("\n")
    runWithInput(input)
      .split('\n').toList.map(decode[Result])
      .flatTraverse[λ[a => Either[NonEmptyList[Error], a]], Result](_.bimap(NonEmptyList.one, a => List.apply(a)))
  }

  def runWithInput(input: String): String = {
    val inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    val outputStream = new ByteArrayOutputStream()
    Console.withIn(inputStream) {
      Console.withOut(outputStream) {
        Main.run(List.empty).unsafeRunSync()
      }
    }
    new String(outputStream.toByteArray, StandardCharsets.UTF_8)
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

    runWithEvents(input) match {
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

    runWithEvents(input) match {
      case Left(errors) => fail(errors.head)
      case Right(results) =>
        assert(
          results == expected
        )
    }
  }

  it should "give correct output for sample input" in {
    val input = """{"account": {"active-card": true, "available-limit": 100}}
                  |{"transaction": {"merchant": "Burger King", "amount": -20, "time": "2019-02-13T10:00:00.000Z"}}
                  |{"transaction": {"merchant": "Habbib's", "amount": -90, "time": "2019-02-13T11:00:00.000Z"}}""".stripMargin

    val expected = """{"account": {"active-card": true, "available-limit": 100}, "violations": []}
                     |{"account": {"active-card": true, "available-limit": 80}, "violations": []}
                     |{"account": {"active-card": true, "available-limit": 80}, "violations": ["insufficient-limit"]}""".stripMargin

    val output = runWithInput(input).trim

    assert(output == expected)
  }
}
