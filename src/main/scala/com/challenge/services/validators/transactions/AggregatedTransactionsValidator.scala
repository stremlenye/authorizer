package com.challenge.services.validators.transactions

import cats.data.ValidatedNel
import com.challenge.algebras.TransactionValidator
import com.challenge.models.{AccountInformation, Transaction, Violation}
import cats.syntax.traverse._
import cats.instances.list._

class AggregatedTransactionsValidator(validators: TransactionValidator*)
  extends TransactionValidator {

  override def validate(i : AccountInformation, t : Transaction) : ValidatedNel[Violation, Transaction] =
    validators.toList.traverse[ValidatedNel[Violation, *], Transaction](_.validate(i, t)).map(_ => t)
}
