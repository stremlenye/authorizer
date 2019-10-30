package com.challenge.services.validators.account

import com.challenge.models.DuplicatedAccount
import org.scalatest.FlatSpec
import com.challenge.test.util.Fixtures._
import cats.instances.string._
import cats.syntax.foldable._

class DuplicateAccountValidatorTest extends FlatSpec {

  it should "return DuplicatedAccount violation if state already contains the account" in {
    val actual = DuplicateAccountValidator.validate(state.activeCard1KLimit, account.activeCard1KLimit)
    actual.fold(
      violations => assert(List(DuplicatedAccount) == violations.toList),
      _ => fail()
    )
  }

  it should "return supplied acount if state is empty " in {
    val actual = DuplicateAccountValidator.validate(state.empty, account.activeCard1KLimit)
    actual.fold(
      violations => fail(violations.map(_.code).intercalate(", ")),
      a => assert(a == account.activeCard1KLimit)
    )
  }

}
