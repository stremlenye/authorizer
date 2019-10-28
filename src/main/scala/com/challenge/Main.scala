package com.challenge


import com.challenge.io.Loop

import cats.data.{EitherT, StateT}
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._

object Main extends IOApp {

  final case class Store(ax: List[String])

  override def run(args : List[String]) : IO[ExitCode] = {
    type F[A] = StateT[IO, Store, A]

    val source: F[String] = StateT.liftF(IO(Console.in.readLine()))
    val sink: String => F[Unit] = a => StateT.liftF(IO(Console.println(s"${Console.YELLOW}${a}${Console.RESET}")).void)
    val processor: String => EitherT[F, ExitCode, String] = {
      case "" => EitherT.leftT(ExitCode.Success)
      case other =>
        val a: F[String] = StateT.modify[IO, Store](s => s.copy(ax = other +: s.ax)).transform { (s, _) => (s, s.ax.mkString("\n")) }
        EitherT.liftF(a)
    }

    Loop.run(source, sink)(processor).run(Store(List())).map { case (_, exitCode: ExitCode) => exitCode}
  }
}
