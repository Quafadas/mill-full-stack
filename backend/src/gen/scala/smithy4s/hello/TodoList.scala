package smithy4s.hello

import smithy4s.Newtype
import smithy4s.schema.Schema._

object TodoList extends Newtype[List[Todo]] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "TodoList")
  val hints : smithy4s.Hints = smithy4s.Hints.empty
  val underlyingSchema : smithy4s.Schema[List[Todo]] = list(Todo.schema).withId(id).addHints(hints)
  implicit val schema : smithy4s.Schema[TodoList] = bijection(underlyingSchema, TodoList(_), (_ : TodoList).value)
}