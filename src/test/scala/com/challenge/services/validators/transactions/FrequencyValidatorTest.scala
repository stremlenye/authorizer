package com.challenge.services.validators.transactions

import java.time.Duration
import java.time.temporal.ChronoUnit

import cats.data.Validated.{Invalid, Valid}
import com.challenge.models.{AccountInformation, HighFrequency, Transaction}
import com.challenge.test.util.Fixtures.{account, transaction}
import com.challenge.utils.TimeBasedMap
import org.scalatest.FlatSpec

class FrequencyValidatorTest extends FlatSpec {

  it should "detect frequent transactions" in {
    val validator = new FrequencyValidator(Duration.ofMinutes(2), 2)
    val t = transaction.merchantOneM100
    val map = TimeBasedMap.empty[Transaction](ChronoUnit.MINUTES)
    List(
      AccountInformation(account.activeCard1KLimit, map.add(t.time, t).add(t.time, t)),
      AccountInformation(account.activeCard1KLimit, map.add(t.time.plusMinutes(1), t).add(t.time.plusMinutes(2), t)),
      AccountInformation(account.activeCard1KLimit, map.add(t.time.minusMinutes(1), t).add(t.time.minusMinutes(2), t)),
      AccountInformation(account.activeCard1KLimit, map.add(t.time.minusMinutes(1), t).add(t.time.plusMinutes(1), t)),
    ).map(validator.validate(_, t)).foreach {
      case Valid(_) => fail("Found valid")
      case Invalid(violations) => assert(violations.toList == List(HighFrequency))
    }
  }

  it should "return transaction if no frequent transactions found" in {
    val validator = new FrequencyValidator(Duration.ofMinutes(2), 1)
    val t = transaction.merchantOneM100
    val map = TimeBasedMap.empty[Transaction](ChronoUnit.MINUTES)
    val s = AccountInformation(account.activeCard1KLimit, map.add(t.time.minusMinutes(10), t))
    assert(validator.validate(s, t).isValid)
  }

}
