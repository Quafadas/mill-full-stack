package smithy4s.hello

import TodoServiceGen.GetTodoError
import TodoServiceGen.GetTodosError
import smithy4s.schema.Schema._

trait TodoServiceGen[F[_, _, _, _, _]] {
  self =>

  def getTodo(id: String) : F[TodoInput, GetTodoError, Todo, Nothing, Nothing]
  def getTodos() : F[Unit, GetTodosError, Todos, Nothing, Nothing]

  def transform[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) : TodoServiceGen[G] = new Transformed(transformation)
  class Transformed[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) extends TodoServiceGen[G] {
    def getTodo(id: String) = transformation[TodoInput, GetTodoError, Todo, Nothing, Nothing](self.getTodo(id))
    def getTodos() = transformation[Unit, GetTodosError, Todos, Nothing, Nothing](self.getTodos())
  }
}

object TodoServiceGen extends smithy4s.Service[TodoServiceGen, TodoServiceOperation] {

  def apply[F[_]](implicit F: smithy4s.Monadic[TodoServiceGen, F]): F.type = F

  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "TodoService")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy4s.api.SimpleRestJson(),
  )

  val endpoints = List(
    GetTodo,
    GetTodos,
  )

  val version: String = "1.0.0"

  def endpoint[I, E, O, SI, SO](op : TodoServiceOperation[I, E, O, SI, SO]) = op match {
    case GetTodo(input) => (input, GetTodo)
    case GetTodos() => ((), GetTodos)
  }

  object reified extends TodoServiceGen[TodoServiceOperation] {
    def getTodo(id: String) = GetTodo(TodoInput(id))
    def getTodos() = GetTodos()
  }

  def transform[P[_, _, _, _, _]](transformation: smithy4s.Transformation[TodoServiceOperation, P]): TodoServiceGen[P] = reified.transform(transformation)

  def transform[P[_, _, _, _, _], P1[_, _, _, _, _]](alg: TodoServiceGen[P], transformation: smithy4s.Transformation[P, P1]): TodoServiceGen[P1] = alg.transform(transformation)

  def asTransformation[P[_, _, _, _, _]](impl : TodoServiceGen[P]): smithy4s.Transformation[TodoServiceOperation, P] = new smithy4s.Transformation[TodoServiceOperation, P] {
    def apply[I, E, O, SI, SO](op : TodoServiceOperation[I, E, O, SI, SO]) : P[I, E, O, SI, SO] = op match  {
      case GetTodo(TodoInput(id)) => impl.getTodo(id)
      case GetTodos() => impl.getTodos()
    }
  }
  case class GetTodo(input: TodoInput) extends TodoServiceOperation[TodoInput, GetTodoError, Todo, Nothing, Nothing]
  object GetTodo extends smithy4s.Endpoint[TodoServiceOperation, TodoInput, GetTodoError, Todo, Nothing, Nothing] with smithy4s.Errorable[GetTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "GetTodo")
    val input: smithy4s.Schema[TodoInput] = TodoInput.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Todo] = Todo.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/todo/{id}"), None),
      smithy.api.Readonly(),
    )
    def wrap(input: TodoInput) = GetTodo(input)
    override val errorable: Option[smithy4s.Errorable[GetTodoError]] = Some(this)
    val error: smithy4s.UnionSchema[GetTodoError] = GetTodoError.schema
    def liftError(throwable: Throwable) : Option[GetTodoError] = throwable match {
      case e: BadInput => Some(GetTodoError.BadInputCase(e))
      case _ => None
    }
    def unliftError(e: GetTodoError) : Throwable = e match {
      case GetTodoError.BadInputCase(e) => e
    }
  }
  sealed trait GetTodoError extends scala.Product with scala.Serializable
  object GetTodoError extends smithy4s.ShapeTag.Companion[GetTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "GetTodoError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class BadInputCase(badInput: BadInput) extends GetTodoError

    object BadInputCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[BadInputCase] = bijection(BadInput.schema.addHints(hints), BadInputCase(_), _.badInput)
      val alt = schema.oneOf[GetTodoError]("BadInput")
    }

    implicit val schema: smithy4s.UnionSchema[GetTodoError] = union(
      BadInputCase.alt,
    ){
      case c : BadInputCase => BadInputCase.alt(c)
    }
  }
  case class GetTodos() extends TodoServiceOperation[Unit, GetTodosError, Todos, Nothing, Nothing]
  object GetTodos extends smithy4s.Endpoint[TodoServiceOperation, Unit, GetTodosError, Todos, Nothing, Nothing] with smithy4s.Errorable[GetTodosError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "GetTodos")
    val input: smithy4s.Schema[Unit] = unit.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Todos] = Todos.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/todo"), None),
      smithy.api.Readonly(),
    )
    def wrap(input: Unit) = GetTodos()
    override val errorable: Option[smithy4s.Errorable[GetTodosError]] = Some(this)
    val error: smithy4s.UnionSchema[GetTodosError] = GetTodosError.schema
    def liftError(throwable: Throwable) : Option[GetTodosError] = throwable match {
      case e: BadInput => Some(GetTodosError.BadInputCase(e))
      case _ => None
    }
    def unliftError(e: GetTodosError) : Throwable = e match {
      case GetTodosError.BadInputCase(e) => e
    }
  }
  sealed trait GetTodosError extends scala.Product with scala.Serializable
  object GetTodosError extends smithy4s.ShapeTag.Companion[GetTodosError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "GetTodosError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class BadInputCase(badInput: BadInput) extends GetTodosError

    object BadInputCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[BadInputCase] = bijection(BadInput.schema.addHints(hints), BadInputCase(_), _.badInput)
      val alt = schema.oneOf[GetTodosError]("BadInput")
    }

    implicit val schema: smithy4s.UnionSchema[GetTodosError] = union(
      BadInputCase.alt,
    ){
      case c : BadInputCase => BadInputCase.alt(c)
    }
  }
}

sealed trait TodoServiceOperation[Input, Err, Output, StreamedInput, StreamedOutput]
