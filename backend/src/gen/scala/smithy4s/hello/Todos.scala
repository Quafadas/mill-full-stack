package smithy4s.hello

import smithy4s.schema.Schema._

case class Todos(todos: Option[List[Todo]] = None)
object Todos extends smithy4s.ShapeTag.Companion[Todos] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "Todos")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Todos] = struct(
    TodoList.underlyingSchema.optional[Todos]("todos", _.todos),
  ){
    Todos.apply
  }.withId(id).addHints(hints)
}