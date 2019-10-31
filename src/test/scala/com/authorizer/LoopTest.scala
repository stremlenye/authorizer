package com.authorizer

import cats.data.EitherT
import cats.effect.{ExitCode, SyncIO}
import com.authorizer.utils.Loop
import org.scalatest.FlatSpec
import org.scalatest.tagobjects.Slow

class LoopTest extends FlatSpec {
  it should "be stack safe" taggedAs Slow  in {
    val size = 10000
    val feed = Stream.from(0).take(size + 1).iterator
    val result = Loop
      .run(SyncIO(feed.next()), (_: Int) => SyncIO.unit) {
        case `size` => EitherT.left(SyncIO.pure(ExitCode.Success))
        case other =>
          if (other % 1000 == 0) println(s"${Console.YELLOW}>>>> LoopTest.scala:15  $other ${Console.RESET}")
          EitherT.right(SyncIO.pure(other))
      }.unsafeRunSync()
    assert(result == ExitCode.Success)
    assert(feed.isEmpty)
  }
}
