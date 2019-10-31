package com.authorizer.algebras

import cats.tagless.{Derive, FunctorK}
import com.authorizer.models.{Account, Result, Transaction}

trait AccountAlgebra[F[_]] {
  def createAccount(a: Account): F[Result]
  def commitTransaction(t: Transaction): F[Result]
}

object AccountAlgebra {
  implicit val functorK: FunctorK[AccountAlgebra] = Derive.functorK[AccountAlgebra]
}


