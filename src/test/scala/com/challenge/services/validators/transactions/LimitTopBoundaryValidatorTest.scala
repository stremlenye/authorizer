package com.challenge.services.validators.transactions

import cats.data.Validated.{Invalid, Valid}
import com.challenge.models._
import com.challenge.test.util.Fixtures.{account, transaction}
import org.scalatest.FlatSpec

class LimitTopBoundaryValidatorTest extends FlatSpec {
  it should "detect violation of top boundary of account limit" in {
    val validator = LimitTopBoundaryValidator
    val t = transaction.merchantOneP100
    validator.validate(AccountInformation(account.activeCardMaxLimit), t) match {
      case Valid(_) => fail("Found valid")
      case Invalid(violations) => assert(violations.toList == List(ExceedLimitTopBoundary))
    }
  }

  it should "return transaction if no violation of top boundary of account limit detected" in {
    val validator = LimitTopBoundaryValidator
    val t = transaction.merchantOneM100
    val result = validator.validate(AccountInformation(account.activeCardZeroLimit), t)
    assert(result.isValid)
  }
}
