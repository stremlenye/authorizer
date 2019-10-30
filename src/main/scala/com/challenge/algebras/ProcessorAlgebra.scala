package com.challenge.algebras

trait ProcessorAlgebra[F[_], A, B] {
  def process(a: A): F[A]
}
