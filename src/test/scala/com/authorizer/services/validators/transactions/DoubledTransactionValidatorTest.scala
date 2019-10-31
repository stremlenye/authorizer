package com.authorizer.services.validators.transactions

import com.authorizer.test.util.Fixtures._
import java.time.Duration
import java.time.temporal.ChronoUnit

import cats.data.Validated.{Invalid, Valid}
import com.authorizer.models.{AccountInformation, DoubledTransaction, Transaction}
import com.authorizer.utils.TimeBasedMap
import org.scalatest.FlatSpec

class DoubledTransactionValidatorTest extends FlatSpec {
  it should "detect doubled transactions" in {
    val validator = new DoubledTransactionValidator(Duration.ofMinutes(2), 1)
    val t = transaction.merchantOneM100
    val map = TimeBasedMap.empty[Transaction](ChronoUnit.MINUTES)
    List(
      AccountInformation(account.activeCard1KLimit, map.add(t.time, t).add(t.time, t)),
      AccountInformation(account.activeCard1KLimit, map.add(t.time.plusMinutes(1), t).add(t.time.plusMinutes(2), t)),
      AccountInformation(account.activeCard1KLimit, map.add(t.time.minusMinutes(1), t).add(t.time.minusMinutes(2), t)),
      AccountInformation(account.activeCard1KLimit, map.add(t.time.minusMinutes(1), t).add(t.time.plusMinutes(1), t)),
    ).map(validator.validate(_, t)).foreach {
      case Valid(_) => fail("Found valid")
      case Invalid(violations) => assert(violations.toList == List(DoubledTransaction))
    }
  }

  it should "return transaction if no doubled found" in {
    val validator = new DoubledTransactionValidator(Duration.ofMinutes(2), 1)
    val t = transaction.merchantOneM100
    val map = TimeBasedMap.empty[Transaction](ChronoUnit.MINUTES)
    val s = AccountInformation(account.activeCard1KLimit, map.add(t.time.minusMinutes(10), t))
    assert(validator.validate(s, t).isValid)
  }
}
