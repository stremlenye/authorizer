package com.challenge.test.util

import cats.data.NonEmptyList
import com.challenge.models.Result.{Failure, Success}
import com.challenge.models.{Account, Result, Violation}
import org.scalatest.{Assertion, Assertions}
import cats.syntax.foldable._
import cats.instances.string._
import org.scalactic.source

trait ResultAssertions { self: Assertions =>
  def expectSuccess(result: Result)(f: Account => Assertion)(implicit pos: source.Position): Assertion =
    result match {
      case Success(account) => f(account)
      case Failure(_, violations) => fail(s"Failed with violations: ${violations.map(_.code).intercalate(", ")}")
    }

  def expectFailure(result: Result)(f: (Option[Account], NonEmptyList[Violation]) => Assertion)(implicit pos: source.Position): Assertion =
    result match {
      case a@Success(_) => fail(s"Expected failure but encountered: $a")
      case Failure(account, violations) => f(account, violations)
    }

  def expectViolations(result: Result)(violations: Violation*)(implicit pos: source.Position): Assertion =
    expectFailure(result)((_, vs) => assert(violations.toList == vs.toList))
}
