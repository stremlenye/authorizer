package com.challenge.utils

import cats.{Functor, ~>}
import cats.data.EitherT

package object transformations {
  def errorLifting[F[_]: Functor, E, X](f: E => X): EitherT[F, E, *] ~> EitherT[F, X, *] =
    λ[EitherT[F, E, *] ~> EitherT[F, X, *]](_.leftMap(f))
}
