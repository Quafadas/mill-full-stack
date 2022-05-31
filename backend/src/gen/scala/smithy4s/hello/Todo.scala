package smithy4s.hello

import smithy4s.schema.Schema._

case class Todo(id: String, description: Option[String] = None, complete: Option[Boolean] = None)
object Todo extends smithy4s.ShapeTag.Companion[Todo] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "Todo")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Todo] = struct(
    string.required[Todo]("id", _.id).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
    string.optional[Todo]("description", _.description),
    boolean.optional[Todo]("complete", _.complete),
  ){
    Todo.apply
  }.withId(id).addHints(hints)
}