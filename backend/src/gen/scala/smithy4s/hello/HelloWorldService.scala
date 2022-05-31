package smithy4s.hello

import HelloWorldServiceGen.GreetError
import HelloWorldServiceGen.HelloError
import smithy4s.schema.Schema._

trait HelloWorldServiceGen[F[_, _, _, _, _]] {
  self =>

  def hello(name: String, town: Option[String] = None) : F[Person, HelloError, Greeting, Nothing, Nothing]
  def greet(name: String) : F[GreetInput, GreetError, GreetOutput, Nothing, Nothing]

  def transform[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) : HelloWorldServiceGen[G] = new Transformed(transformation)
  class Transformed[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) extends HelloWorldServiceGen[G] {
    def hello(name: String, town: Option[String] = None) = transformation[Person, HelloError, Greeting, Nothing, Nothing](self.hello(name, town))
    def greet(name: String) = transformation[GreetInput, GreetError, GreetOutput, Nothing, Nothing](self.greet(name))
  }
}

object HelloWorldServiceGen extends smithy4s.Service[HelloWorldServiceGen, HelloWorldServiceOperation] {

  def apply[F[_]](implicit F: smithy4s.Monadic[HelloWorldServiceGen, F]): F.type = F

  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "HelloWorldService")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy4s.api.SimpleRestJson(),
  )

  val endpoints = List(
    Hello,
    Greet,
  )

  val version: String = "1.0.0"

  def endpoint[I, E, O, SI, SO](op : HelloWorldServiceOperation[I, E, O, SI, SO]) = op match {
    case Hello(input) => (input, Hello)
    case Greet(input) => (input, Greet)
  }

  object reified extends HelloWorldServiceGen[HelloWorldServiceOperation] {
    def hello(name: String, town: Option[String] = None) = Hello(Person(name, town))
    def greet(name: String) = Greet(GreetInput(name))
  }

  def transform[P[_, _, _, _, _]](transformation: smithy4s.Transformation[HelloWorldServiceOperation, P]): HelloWorldServiceGen[P] = reified.transform(transformation)

  def transform[P[_, _, _, _, _], P1[_, _, _, _, _]](alg: HelloWorldServiceGen[P], transformation: smithy4s.Transformation[P, P1]): HelloWorldServiceGen[P1] = alg.transform(transformation)

  def asTransformation[P[_, _, _, _, _]](impl : HelloWorldServiceGen[P]): smithy4s.Transformation[HelloWorldServiceOperation, P] = new smithy4s.Transformation[HelloWorldServiceOperation, P] {
    def apply[I, E, O, SI, SO](op : HelloWorldServiceOperation[I, E, O, SI, SO]) : P[I, E, O, SI, SO] = op match  {
      case Hello(Person(name, town)) => impl.hello(name, town)
      case Greet(GreetInput(name)) => impl.greet(name)
    }
  }
  case class Hello(input: Person) extends HelloWorldServiceOperation[Person, HelloError, Greeting, Nothing, Nothing]
  object Hello extends smithy4s.Endpoint[HelloWorldServiceOperation, Person, HelloError, Greeting, Nothing, Nothing] with smithy4s.Errorable[HelloError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "Hello")
    val input: smithy4s.Schema[Person] = Person.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Greeting] = Greeting.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("POST"), smithy.api.NonEmptyString("/{name}"), Some(200)),
    )
    def wrap(input: Person) = Hello(input)
    override val errorable: Option[smithy4s.Errorable[HelloError]] = Some(this)
    val error: smithy4s.UnionSchema[HelloError] = HelloError.schema
    def liftError(throwable: Throwable) : Option[HelloError] = throwable match {
      case e: BadInput => Some(HelloError.BadInputCase(e))
      case _ => None
    }
    def unliftError(e: HelloError) : Throwable = e match {
      case HelloError.BadInputCase(e) => e
    }
  }
  sealed trait HelloError extends scala.Product with scala.Serializable
  object HelloError extends smithy4s.ShapeTag.Companion[HelloError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "HelloError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class BadInputCase(badInput: BadInput) extends HelloError

    object BadInputCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[BadInputCase] = bijection(BadInput.schema.addHints(hints), BadInputCase(_), _.badInput)
      val alt = schema.oneOf[HelloError]("BadInput")
    }

    implicit val schema: smithy4s.UnionSchema[HelloError] = union(
      BadInputCase.alt,
    ){
      case c : BadInputCase => BadInputCase.alt(c)
    }
  }
  case class Greet(input: GreetInput) extends HelloWorldServiceOperation[GreetInput, GreetError, GreetOutput, Nothing, Nothing]
  object Greet extends smithy4s.Endpoint[HelloWorldServiceOperation, GreetInput, GreetError, GreetOutput, Nothing, Nothing] with smithy4s.Errorable[GreetError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "Greet")
    val input: smithy4s.Schema[GreetInput] = GreetInput.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[GreetOutput] = GreetOutput.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/hello/{name}"), None),
      smithy.api.Readonly(),
    )
    def wrap(input: GreetInput) = Greet(input)
    override val errorable: Option[smithy4s.Errorable[GreetError]] = Some(this)
    val error: smithy4s.UnionSchema[GreetError] = GreetError.schema
    def liftError(throwable: Throwable) : Option[GreetError] = throwable match {
      case e: BadInput => Some(GreetError.BadInputCase(e))
      case _ => None
    }
    def unliftError(e: GreetError) : Throwable = e match {
      case GreetError.BadInputCase(e) => e
    }
  }
  sealed trait GreetError extends scala.Product with scala.Serializable
  object GreetError extends smithy4s.ShapeTag.Companion[GreetError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "GreetError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class BadInputCase(badInput: BadInput) extends GreetError

    object BadInputCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[BadInputCase] = bijection(BadInput.schema.addHints(hints), BadInputCase(_), _.badInput)
      val alt = schema.oneOf[GreetError]("BadInput")
    }

    implicit val schema: smithy4s.UnionSchema[GreetError] = union(
      BadInputCase.alt,
    ){
      case c : BadInputCase => BadInputCase.alt(c)
    }
  }
}

sealed trait HelloWorldServiceOperation[Input, Err, Output, StreamedInput, StreamedOutput]
