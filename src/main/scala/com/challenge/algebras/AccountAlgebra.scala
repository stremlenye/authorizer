package com.challenge.algebras

import cats.tagless.{Derive, FunctorK}
import com.challenge.models.{Account, Result, Transaction}

trait AccountAlgebra[F[_]] {
  def createAccount(a: Account): F[Result]
  def commitTransaction(t: Transaction): F[Result]
}

object AccountAlgebra {
  implicit val functorK: FunctorK[AccountAlgebra] = Derive.functorK[AccountAlgebra]
}


