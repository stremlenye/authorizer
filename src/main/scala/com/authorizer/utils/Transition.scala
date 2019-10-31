package com.authorizer.utils

import cats.data.Kleisli
import cats.syntax.applicative._
import cats.{Applicative, ApplicativeError}
import cats.syntax.either._
import io.circe._
import io.circe.parser._
import io.circe.syntax._

object Transition {

  type Transition[F[_], A, B] = Kleisli[F, A, B]

  def stringToJson[F[_], E](fe: ParsingFailure => E)(implicit F: ApplicativeError[F, E]): Transition[F, String, Json] =
    Kleisli(a => F.fromEither(parse(a).leftMap(fe)))

  def jsonToA[F[_], A : Decoder, E](fe: DecodingFailure => E)(implicit F: ApplicativeError[F, E]): Transition[F, Json, A] =
    Kleisli(a => F.fromEither(a.as[A].leftMap(fe)))

  def aToJson[F[_] : Applicative, A : Encoder]: Transition[F, A, Json] =
    Kleisli(_.asJson.pure)

  def jsonToString[F[_] : Applicative](p: Printer): Transition[F, Json, String] =
    Kleisli(_.pretty(p).pure)
}
