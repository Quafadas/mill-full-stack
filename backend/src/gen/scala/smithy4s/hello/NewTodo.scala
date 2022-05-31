package smithy4s.hello

import smithy4s.schema.Schema._

case class NewTodo(description: Option[String] = None, complete: Option[Boolean] = None)
object NewTodo extends smithy4s.ShapeTag.Companion[NewTodo] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "NewTodo")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[NewTodo] = struct(
    string.optional[NewTodo]("description", _.description),
    boolean.optional[NewTodo]("complete", _.complete),
  ){
    NewTodo.apply
  }.withId(id).addHints(hints)
}