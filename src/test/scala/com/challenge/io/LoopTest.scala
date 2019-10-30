package com.challenge.io

import cats.data.EitherT
import cats.effect.{ExitCode, SyncIO}
import com.challenge.utils.Loop
import org.scalatest.FunSuite

class LoopTest extends FunSuite {
  test("should be stack safe") {
    val size = 10000
    val feed = Stream.from(0).take(size + 1).iterator
    val result = Loop
      .run(SyncIO(feed.next()), (_: Int) => SyncIO.unit) {
        case `size` => EitherT.left(SyncIO.pure(ExitCode.Success))
        case other  =>
          if(other % 1000 == 0) println(s"${Console.YELLOW}>>>> LoopTest.scala:15  ${other} ${Console.RESET}")
          EitherT.right(SyncIO.pure(other))
      }.unsafeRunSync()
    assert(result == ExitCode.Success)
    assert(feed.isEmpty)
  }
}
