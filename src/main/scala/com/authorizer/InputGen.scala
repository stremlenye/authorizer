package com.authorizer

import java.time.ZonedDateTime

import com.authorizer.models.Event.{CommitTransaction, CreateAccount}
import com.authorizer.models.{Account, Event, Transaction}
import io.circe.syntax._

import scala.util.Random

object InputGen extends App {
  val merchants: Stream[String] = Stream("merchant1", "merchant2", "merchant3").append(merchants)
  val timestamps = Stream.iterate(ZonedDateTime.now().minusHours(1))(_.plusMinutes(1))

  val transactions: Stream[Event] = merchants.zip(timestamps).map {
    case (merchant, timestamp) => CommitTransaction(Transaction(merchant, Random.nextInt(), timestamp))
  }

  val account = CreateAccount(Account(activeCard = true, availableLimit = 10000))

  val events = account #:: transactions

  events.map(_.asJson.noSpaces).take(1000).foreach(Console.out.println)
}
