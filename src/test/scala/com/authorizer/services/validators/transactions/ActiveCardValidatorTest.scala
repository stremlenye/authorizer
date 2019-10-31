package com.authorizer.services.validators.transactions

import cats.data.Validated.{Invalid, Valid}
import com.authorizer.models._
import com.authorizer.test.util.Fixtures.{account, transaction}
import org.scalatest.FlatSpec

class ActiveCardValidatorTest extends FlatSpec {
  it should "invalidate transaction against inactive account" in {
    val validator = ActiveCardValidator
    val t = transaction.merchantOneP100
    validator.validate(AccountInformation(account.inactiveCardZeroLimit), t) match {
      case Valid(_) => fail("Found valid")
      case Invalid(violations) => assert(violations.toList == List(CardNotActive))
    }
  }

  it should "return transaction if account is active" in {
    val validator = LimitTopBoundaryValidator
    val t = transaction.merchantOneM100
    val result = validator.validate(AccountInformation(account.activeCardZeroLimit), t)
    assert(result.isValid)
  }
}
