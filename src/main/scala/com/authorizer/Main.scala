package com.authorizer

import java.time.Duration

import cats.data.{EitherT, StateT}
import cats.effect.{ExitCode, IO, IOApp}
import io.circe._
import cats.syntax.functor._
import com.authorizer.algebras.AccountAlgebra
import com.authorizer.models.{Event, Result}
import com.authorizer.services.{AccountService, InMemoryAccountInformationRepo}
import com.authorizer.utils.Transition.Transition
import com.authorizer.utils.{Loop, Transition}
import cats.mtl.instances.state._
import cats.tagless.syntax.functorK._
import cats.~>
import com.authorizer.services.validators.account.DuplicateAccountValidator
import com.authorizer.services.validators.accountinformation.AccountInformationExistenceValidator
import com.authorizer.services.validators.transactions._

object Main extends IOApp {

  val service: AccountAlgebra[StoreContext] = {
    val repo = new InMemoryAccountInformationRepo[StoreContext]
    val transactionValidator = new AggregatedTransactionsValidator(
      ActiveCardValidator,
      LimitValidator,
      new FrequencyValidator(Duration.ofMinutes(2), 3),
      new DoubledTransactionValidator(Duration.ofMinutes(2), 1),
      LimitTopBoundaryValidator
    )
    new AccountService(repo, DuplicateAccountValidator, AccountInformationExistenceValidator, transactionValidator): AccountAlgebra[
      StoreContext
    ]
  }

  override def run(args: List[String]): IO[ExitCode] = {

    val contextLifting: StoreContext ~> AppContext =
      λ[StoreContext ~> AppContext] { EitherT.liftF(_) }

    val source: StoreContext[String] = StateT.liftF(IO(Console.in.readLine()))
    val sink: String => StoreContext[Unit] = a => StateT.liftF(IO(Console.println(a)).void)

    def errorToExitCode[E](e: E): ExitCode = ExitCode.Error

    val p: Transition[AppContext, String, String] =
      Transition.stringToJson[AppContext, ExitCode](errorToExitCode) andThen
        Transition.jsonToA[AppContext, Event, ExitCode](errorToExitCode) andThen
        Dispatcher(service.mapK(contextLifting)) andThen
        Transition.aToJson[AppContext, Result] andThen
        Transition.jsonToString[AppContext](Printer.noSpaces.copy(colonRight = " ", objectCommaRight = " ", arrayCommaRight = " "))

    Loop.run(source, sink)(p.run).run(None).map { case (_, exitCode: ExitCode) => exitCode }
  }
}
