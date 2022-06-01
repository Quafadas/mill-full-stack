package smithy4s.hello

import smithy4s.schema.Schema._

case class GreetInput(name: String)
object GreetInput extends smithy4s.ShapeTag.Companion[GreetInput] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "GreetInput")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[GreetInput] = struct(
    string.required[GreetInput]("name", _.name).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
  ){
    GreetInput.apply
  }.withId(id).addHints(hints)
}