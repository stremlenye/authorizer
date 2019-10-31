package com.authorizer.services.validators.transactions

import cats.data.Validated.{Invalid, Valid}
import com.authorizer.models._
import com.authorizer.test.util.Fixtures.{account, transaction}
import org.scalatest.FlatSpec

class LimitValidatorTest extends FlatSpec {
  it should "detect violation of available limit boundary" in {
    val validator = LimitValidator
    val t = transaction.merchantOneM100
    validator.validate(AccountInformation(account.activeCardZeroLimit), t) match {
      case Valid(_) => fail("Found valid")
      case Invalid(violations) => assert(violations.toList == List(InsufficientLimit))
    }
  }

  it should "return transaction if no violation of available limit boundary detected" in {
    val validator = LimitValidator
    val t = transaction.merchantOneM100
    val result = validator.validate(AccountInformation(account.activeCard1KLimit), t)
    assert(result.isValid)
  }
}
