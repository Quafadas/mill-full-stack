package smithy4s.hello


trait HelloWorldServiceGen[F[_, _, _, _, _]] {
  self =>

  def hello(name: String, town: Option[String] = None) : F[Person, Nothing, Greeting, Nothing, Nothing]

  def transform[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) : HelloWorldServiceGen[G] = new Transformed(transformation)
  class Transformed[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) extends HelloWorldServiceGen[G] {
    def hello(name: String, town: Option[String] = None) = transformation[Person, Nothing, Greeting, Nothing, Nothing](self.hello(name, town))
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
  )

  val version: String = "1.0.0"

  def endpoint[I, E, O, SI, SO](op : HelloWorldServiceOperation[I, E, O, SI, SO]) = op match {
    case Hello(input) => (input, Hello)
  }

  object reified extends HelloWorldServiceGen[HelloWorldServiceOperation] {
    def hello(name: String, town: Option[String] = None) = Hello(Person(name, town))
  }

  def transform[P[_, _, _, _, _]](transformation: smithy4s.Transformation[HelloWorldServiceOperation, P]): HelloWorldServiceGen[P] = reified.transform(transformation)

  def transform[P[_, _, _, _, _], P1[_, _, _, _, _]](alg: HelloWorldServiceGen[P], transformation: smithy4s.Transformation[P, P1]): HelloWorldServiceGen[P1] = alg.transform(transformation)

  def asTransformation[P[_, _, _, _, _]](impl : HelloWorldServiceGen[P]): smithy4s.Transformation[HelloWorldServiceOperation, P] = new smithy4s.Transformation[HelloWorldServiceOperation, P] {
    def apply[I, E, O, SI, SO](op : HelloWorldServiceOperation[I, E, O, SI, SO]) : P[I, E, O, SI, SO] = op match  {
      case Hello(Person(name, town)) => impl.hello(name, town)
    }
  }
  case class Hello(input: Person) extends HelloWorldServiceOperation[Person, Nothing, Greeting, Nothing, Nothing]
  object Hello extends smithy4s.Endpoint[HelloWorldServiceOperation, Person, Nothing, Greeting, Nothing, Nothing] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "Hello")
    val input: smithy4s.Schema[Person] = Person.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Greeting] = Greeting.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("POST"), smithy.api.NonEmptyString("/{name}"), Some(200)),
    )
    def wrap(input: Person) = Hello(input)
  }
}

sealed trait HelloWorldServiceOperation[Input, Err, Output, StreamedInput, StreamedOutput]
