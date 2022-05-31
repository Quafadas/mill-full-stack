package smithy4s.hello

import smithy4s.schema.Schema._

case class TodoInput(id: String)
object TodoInput extends smithy4s.ShapeTag.Companion[TodoInput] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "TodoInput")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[TodoInput] = struct(
    string.required[TodoInput]("id", _.id).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
  ){
    TodoInput.apply
  }.withId(id).addHints(hints)
}