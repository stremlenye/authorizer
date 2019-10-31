package com

import cats.data.{EitherT, StateT}
import cats.effect.{ExitCode, IO}
import com.authorizer.models.AccountInformation

package object authorizer {
  type ExecutionContextT[F[_], A] = EitherT[F, ExitCode, A]
  type StoreContext[A] = StateT[IO, Option[AccountInformation], A]
  type AppContext[A] = ExecutionContextT[StoreContext, A]
}
