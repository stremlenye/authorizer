package com.challenge.algebras

import cats.tagless.{Derive, FunctorK}
import com.challenge.models.{Account, Transaction}

trait AccountAlgebra[F[_]] {
  def createAccount(a: Account): F[Account]
  def commitTransaction(t: Transaction): F[Account]
}

object AccountAlgebra {
  implicit val functorK: FunctorK[AccountAlgebra] = Derive.functorK[AccountAlgebra]
}


