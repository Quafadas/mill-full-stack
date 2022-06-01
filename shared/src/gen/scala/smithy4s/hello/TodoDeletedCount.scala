package smithy4s.hello

import smithy4s.schema.Schema._

case class TodoDeletedCount(count: Int)
object TodoDeletedCount extends smithy4s.ShapeTag.Companion[TodoDeletedCount] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "TodoDeletedCount")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[TodoDeletedCount] = struct(
    int.required[TodoDeletedCount]("count", _.count).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
  ){
    TodoDeletedCount.apply
  }.withId(id).addHints(hints)
}