package com.authorizer.algebras

trait ProcessorAlgebra[F[_], A, B] {
  def process(a: A): F[A]
}
