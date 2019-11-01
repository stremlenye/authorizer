# Authorizer
## Assumptions
* transactions come in a not chronological sequence (not sorted by time)
* the available limit has a top boundary of `Int.MaxValue`
* account’s available limit is calculated based on ‘first come – first served’ principal, which means the funds sufficiency check performed against current available limit and the timestamp of the transaction is not taken in account
* The transactions cache stored in memory virtually never causes `OutOfMemoryException` so no cache deprecation is necessary

## Design
### Domain

As it was inferred from the requirements of the task the solution operates over two main types of objects: account and transaction, which were extracted into respective case classes.
To accommodate the input format described, solution presents the type `Event` with its two subtypes named after the action being targeted: `CreateAccount` and `CommitTransaction`. Said types structure serve the purpose of exhaustiveness of matching the payloads with respective actions of the service, leaving the room for the further extensions.
The `Result` type logically serves the purpose of correct formatting of the output providing two options: `Success` and `Failure`. The former one includes the violations list while the other embeds that field only on serialisation stage for conformity with the requirements.

### Infrastructure design

To provide the internal services with the domain-relevant data and enable their output processing these questions have to be solved:
* continuous, line by line, reading from standard input and subsequent writing to the standard output
* deserialisation of the lines into the expected types of events (see: `Event`)
* serialisation of the processing results back to strings
* Managing the state between events

Addressing the first point, solution provides the generic infrastructure uniting the producer, consumer and processor giving the effect as the return type which enables the consumer use the referentially transparent computations for the IO operations.

To address the second and third points of the list the composition approach was chosen: the side-effectful transitions between types are represented by the type `Transition[F[_], A, B] = Kleisli[F, A, B]` where type `F[_]` includes the `ExecutionContextT[F[_], A] = EitherT[F, ExitCode, A]` as part of context which forces the failed computations to signal the application to stop operating and exit with non-zero exit code.

As far as the requirement demands the application state to be kept in memory, the composed computation was designed  in the context of `StateT[IO, Option[AccountInformation], A]` which enables enables referentially transparent transitions of immutable state. As the positive side of effect of that approach: on the tests side that allows to seed the computation with the desired initial state without running prior computations.

### Logic implementation design

Inferred from the types of events the incoming requests are served by two methods united under the `AccountService`: `createAccount` and `commitTransaction`. To enforce the validation rules said service is provided with three types of validators: `AccountValidator`, `AccountInformationValidator` and `TransactionValidator`. Each of them designed with the idea of cumulative violations report in mind. The idea is to provide the granular set of composable validators united under the one base type to keep the validation logic simple and extendable.

The access to the application state is provided by the `InMemoryAccountInformationRepo` which uses `MonadState`  type class to manage the `AccountInformation` model which aggregates the current account information as well as the transactions cache.

To prevent the throughput degradation of the application with the transaction cache growth, it was organised as the map structure with the transactions kept in the buckets defined by their timestamp rounded up to the configured granularity (see: `com.authorizer.utils.TimeBasedMap`). That structure exposes the methods like `sliding` and `getInInterval` giving the validators access to the subsets of transactions in chronological order within the provided interval.


## Build
To build the solution you would need the `sbt` tool installed.
Then: 
```shell
$ sbt
> compile
> assembly 
```

## Testing
To run unit and integration tests:
```shell
$ sbt
> test
> it:test
```

For the demonstration purpose and smoke tests there is a possibility to get the application assembled and run with
prepared script:
```shell
$ sbt assembly
./bin/example.sh
```

## Running
To run application you could use previously built asset which you could find in `/target/scala-2.12/authorizer.jar` (see `./bin/example.sh` for reference).
As an alternative `sbt run` could be used.

## Notes
1. Solution provides the simple tool to generate random input for the application.
To run it use:
```shell
sbt "set mainClass := Some(\"com.authorizer.InputGen\"); set assemblyJarName in assembly := \"inputgen.jar\"; assembly"
./target/scala-2.12/inputgen.jar
```

2. Even though some if the implemented components are configurable not external configuration reading options are provided for the sake of simplicity of the solution.
