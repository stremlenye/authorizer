package com.challenge

import cats.data.Kleisli
import com.challenge.algebras.AccountAlgebra
import com.challenge.models.{Event, Result}

object Dispatcher {

  def apply[F[_]](service: AccountAlgebra[F]): Kleisli[F, Event, Result] =
    Kleisli[F, Event, Result] {
      case Event.CreateAccount(a)     => service.createAccount(a)
      case Event.CommitTransaction(t) => service.commitTransaction(t)
    }
}
