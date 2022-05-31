package smithy4s.hello

import smithy4s.schema.Schema._

case class TodoUpdate(id: String, description: Option[String] = None, complete: Option[Boolean] = None)
object TodoUpdate extends smithy4s.ShapeTag.Companion[TodoUpdate] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "TodoUpdate")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[TodoUpdate] = struct(
    string.required[TodoUpdate]("id", _.id).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
    string.optional[TodoUpdate]("description", _.description),
    boolean.optional[TodoUpdate]("complete", _.complete),
  ){
    TodoUpdate.apply
  }.withId(id).addHints(hints)
}