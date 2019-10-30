package com.challenge

import java.time.Duration

import cats.data.{EitherT, Kleisli, StateT}
import cats.effect.{ExitCode, IO, IOApp}
import io.circe._
import cats.syntax.functor._
import com.challenge.algebras.AccountAlgebra
import com.challenge.models.{AccountInformation, Event, Result}
import com.challenge.services.{AccountService, InMemoryAccountInformationRepo}
import com.challenge.utils.Processor.Processor
import com.challenge.utils.{Loop, Processor}
import cats.mtl.instances.state._
import cats.tagless.syntax.functorK._
import cats.~>
import com.challenge.services.validators.account.DuplicateAccountValidator
import com.challenge.services.validators.accountinformation.AccountInformationExistenceValidator
import com.challenge.services.validators.transactions._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    type ExecutionContextT[F[_], A] = EitherT[F, ExitCode, A]
    type StoreContext[A] = StateT[IO, Option[AccountInformation], A]
    type AppContext[A] = ExecutionContextT[StoreContext, A]

    val contextLifting: StoreContext ~> AppContext =
      λ[StoreContext ~> AppContext] { EitherT.liftF(_) }

    val source: StoreContext[String] = StateT.liftF(IO(Console.in.readLine()))
    val sink: String => StoreContext[Unit] = a => StateT.liftF(IO(Console.println(s"${Console.YELLOW}$a${Console.RESET}")).void)

    val repo = new InMemoryAccountInformationRepo[StoreContext]
    val transactionValidator = new AggregatedTransactionsValidator(
      ActiveCardValidator,
      LimitValidator,
      new FrequencyValidator(Duration.ofMinutes(2), 3),
      new DoubledTransactionValidator(Duration.ofMinutes(2), 1)
    )

    val accountService: AccountAlgebra[AppContext] =
      (new AccountService(repo, DuplicateAccountValidator, AccountInformationExistenceValidator, transactionValidator): AccountAlgebra[
        StoreContext
      ]).mapK(contextLifting)

    def errorToExitCode[E](e: E): ExitCode = ExitCode.Error

    val dispatcher: Kleisli[AppContext, Event, Result] = Kleisli[AppContext, Event, Result] {
      case Event.CreateAccount(a)     => accountService.createAccount(a)
      case Event.CommitTransaction(t) => accountService.commitTransaction(t)
    }

    val p: Processor[AppContext, String, String] =
      Processor.stringToJson[AppContext, ExitCode](errorToExitCode) andThen
        Processor.jsonToA[AppContext, Event, ExitCode](errorToExitCode) andThen
        dispatcher andThen
        Processor.aToJson[AppContext, Result] andThen
        Processor.jsonToString[AppContext](Printer.spaces2)

    Loop.run(source, sink)(p.run).run(None).map { case (_, exitCode: ExitCode) => exitCode }
  }
}
