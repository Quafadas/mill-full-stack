package smithy4s.hello

import smithy4s.schema.Schema._

case class Greeting(message: String)
object Greeting extends smithy4s.ShapeTag.Companion[Greeting] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "Greeting")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Greeting] = struct(
    string.required[Greeting]("message", _.message).addHints(smithy.api.Required()),
  ){
    Greeting.apply
  }.withId(id).addHints(hints)
}