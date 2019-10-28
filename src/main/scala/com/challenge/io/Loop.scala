package com.challenge.io

import cats.data.EitherT
import cats.effect.{ExitCode, Sync}
import fs2._

object Loop {

  def run[F[_] : Sync, A, B](source: F[A], sink: B => F[Unit])(f: A => EitherT[F, ExitCode, B]): F[ExitCode] = {
    val producer = Stream.eval(source).repeat
    val processor: Pipe[F, A, Either[ExitCode, B]] = _.evalMap(f(_).value)
    val consumer: Pipe[F, Either[ExitCode, B], Either[ExitCode, Unit]] = _.evalTap(_.fold(_ => Sync[F].unit, sink)).map(_.map(_ => ()))
    val pipeline: Stream[F, Either[ExitCode, Unit]] = producer.through(processor).through(consumer)

    val scanForExitCode: Pipe[F, Either[ExitCode, Unit], ExitCode] = {
      def go(s : Stream[F, Either[ExitCode, Unit]]) : Pull[F, ExitCode, Unit] =
        s.pull.uncons.flatMap {
          case Some((chunk, stream)) =>
            chunk
              .foldLeft[Option[ExitCode]](None) { (a, term) =>
                val candidate = term.swap.toOption
                a match {
                  case None => candidate
                  case Some(exitCode) => candidate.filter(_.code >= exitCode.code).orElse(a)
                }
              }.map(a => Pull.output(Chunk(a))).getOrElse(go(stream))

          case None => Pull.output(Chunk(ExitCode.Success))
        }
      go(_).stream
    }

    pipeline.through(scanForExitCode).compile.fold(ExitCode.Success)((_, a) => a)
  }

}
