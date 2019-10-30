package com.challenge.test.util

import cats.data.StateT
import cats.effect.IO
import com.challenge.models.Transaction
import org.scalatest.Assertion

object syntax {
  implicit class StateTSyncIoRunSyntax[S, A](val fa: StateT[IO, S, Assertion]) extends AnyVal {

    def withState(s: S): Assertion =
      fa.run(s).unsafeRunSync()._2
  }

  implicit class TransactionsSyntax(val t: Transaction) extends AnyVal {
    def laterByMinutes(a: Int): Transaction = t.copy(time = t.time.plusMinutes(a.toLong))
    def earlierByMinutes(a: Int): Transaction = t.copy(time = t.time.minusMinutes(a.toLong))
  }
}
