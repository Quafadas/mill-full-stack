package smithy4s.hello

import TodoServiceGen.CreateTodoError
import TodoServiceGen.DeleteTodoError
import TodoServiceGen.GetTodoError
import TodoServiceGen.GetTodosError
import TodoServiceGen.UpdateTodoError
import smithy4s.schema.Schema._

trait TodoServiceGen[F[_, _, _, _, _]] {
  self =>

  def getTodo(id: String) : F[TodoInput, GetTodoError, Todo, Nothing, Nothing]
  def getTodos() : F[Unit, GetTodosError, Todos, Nothing, Nothing]
  def updateTodo(id: String, description: Option[String] = None, complete: Option[Boolean] = None) : F[Todo, UpdateTodoError, Todo, Nothing, Nothing]
  def createTodo(description: Option[String] = None, complete: Option[Boolean] = None) : F[NewTodo, CreateTodoError, Todo, Nothing, Nothing]
  def deleteTodo(id: String) : F[TodoInput, DeleteTodoError, TodoDeletedCount, Nothing, Nothing]

  def transform[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) : TodoServiceGen[G] = new Transformed(transformation)
  class Transformed[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) extends TodoServiceGen[G] {
    def getTodo(id: String) = transformation[TodoInput, GetTodoError, Todo, Nothing, Nothing](self.getTodo(id))
    def getTodos() = transformation[Unit, GetTodosError, Todos, Nothing, Nothing](self.getTodos())
    def updateTodo(id: String, description: Option[String] = None, complete: Option[Boolean] = None) = transformation[Todo, UpdateTodoError, Todo, Nothing, Nothing](self.updateTodo(id, description, complete))
    def createTodo(description: Option[String] = None, complete: Option[Boolean] = None) = transformation[NewTodo, CreateTodoError, Todo, Nothing, Nothing](self.createTodo(description, complete))
    def deleteTodo(id: String) = transformation[TodoInput, DeleteTodoError, TodoDeletedCount, Nothing, Nothing](self.deleteTodo(id))
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
    UpdateTodo,
    CreateTodo,
    DeleteTodo,
  )

  val version: String = "1.0.0"

  def endpoint[I, E, O, SI, SO](op : TodoServiceOperation[I, E, O, SI, SO]) = op match {
    case GetTodo(input) => (input, GetTodo)
    case GetTodos() => ((), GetTodos)
    case UpdateTodo(input) => (input, UpdateTodo)
    case CreateTodo(input) => (input, CreateTodo)
    case DeleteTodo(input) => (input, DeleteTodo)
  }

  object reified extends TodoServiceGen[TodoServiceOperation] {
    def getTodo(id: String) = GetTodo(TodoInput(id))
    def getTodos() = GetTodos()
    def updateTodo(id: String, description: Option[String] = None, complete: Option[Boolean] = None) = UpdateTodo(Todo(id, description, complete))
    def createTodo(description: Option[String] = None, complete: Option[Boolean] = None) = CreateTodo(NewTodo(description, complete))
    def deleteTodo(id: String) = DeleteTodo(TodoInput(id))
  }

  def transform[P[_, _, _, _, _]](transformation: smithy4s.Transformation[TodoServiceOperation, P]): TodoServiceGen[P] = reified.transform(transformation)

  def transform[P[_, _, _, _, _], P1[_, _, _, _, _]](alg: TodoServiceGen[P], transformation: smithy4s.Transformation[P, P1]): TodoServiceGen[P1] = alg.transform(transformation)

  def asTransformation[P[_, _, _, _, _]](impl : TodoServiceGen[P]): smithy4s.Transformation[TodoServiceOperation, P] = new smithy4s.Transformation[TodoServiceOperation, P] {
    def apply[I, E, O, SI, SO](op : TodoServiceOperation[I, E, O, SI, SO]) : P[I, E, O, SI, SO] = op match  {
      case GetTodo(TodoInput(id)) => impl.getTodo(id)
      case GetTodos() => impl.getTodos()
      case UpdateTodo(Todo(id, description, complete)) => impl.updateTodo(id, description, complete)
      case CreateTodo(NewTodo(description, complete)) => impl.createTodo(description, complete)
      case DeleteTodo(TodoInput(id)) => impl.deleteTodo(id)
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
  case class UpdateTodo(input: Todo) extends TodoServiceOperation[Todo, UpdateTodoError, Todo, Nothing, Nothing]
  object UpdateTodo extends smithy4s.Endpoint[TodoServiceOperation, Todo, UpdateTodoError, Todo, Nothing, Nothing] with smithy4s.Errorable[UpdateTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "UpdateTodo")
    val input: smithy4s.Schema[Todo] = Todo.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Todo] = Todo.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("POST"), smithy.api.NonEmptyString("/todo/{id}"), None),
    )
    def wrap(input: Todo) = UpdateTodo(input)
    override val errorable: Option[smithy4s.Errorable[UpdateTodoError]] = Some(this)
    val error: smithy4s.UnionSchema[UpdateTodoError] = UpdateTodoError.schema
    def liftError(throwable: Throwable) : Option[UpdateTodoError] = throwable match {
      case e: BadInput => Some(UpdateTodoError.BadInputCase(e))
      case _ => None
    }
    def unliftError(e: UpdateTodoError) : Throwable = e match {
      case UpdateTodoError.BadInputCase(e) => e
    }
  }
  sealed trait UpdateTodoError extends scala.Product with scala.Serializable
  object UpdateTodoError extends smithy4s.ShapeTag.Companion[UpdateTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "UpdateTodoError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class BadInputCase(badInput: BadInput) extends UpdateTodoError

    object BadInputCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[BadInputCase] = bijection(BadInput.schema.addHints(hints), BadInputCase(_), _.badInput)
      val alt = schema.oneOf[UpdateTodoError]("BadInput")
    }

    implicit val schema: smithy4s.UnionSchema[UpdateTodoError] = union(
      BadInputCase.alt,
    ){
      case c : BadInputCase => BadInputCase.alt(c)
    }
  }
  case class CreateTodo(input: NewTodo) extends TodoServiceOperation[NewTodo, CreateTodoError, Todo, Nothing, Nothing]
  object CreateTodo extends smithy4s.Endpoint[TodoServiceOperation, NewTodo, CreateTodoError, Todo, Nothing, Nothing] with smithy4s.Errorable[CreateTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "CreateTodo")
    val input: smithy4s.Schema[NewTodo] = NewTodo.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Todo] = Todo.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("PUT"), smithy.api.NonEmptyString("/todo"), None),
    )
    def wrap(input: NewTodo) = CreateTodo(input)
    override val errorable: Option[smithy4s.Errorable[CreateTodoError]] = Some(this)
    val error: smithy4s.UnionSchema[CreateTodoError] = CreateTodoError.schema
    def liftError(throwable: Throwable) : Option[CreateTodoError] = throwable match {
      case e: BadInput => Some(CreateTodoError.BadInputCase(e))
      case _ => None
    }
    def unliftError(e: CreateTodoError) : Throwable = e match {
      case CreateTodoError.BadInputCase(e) => e
    }
  }
  sealed trait CreateTodoError extends scala.Product with scala.Serializable
  object CreateTodoError extends smithy4s.ShapeTag.Companion[CreateTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "CreateTodoError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class BadInputCase(badInput: BadInput) extends CreateTodoError

    object BadInputCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[BadInputCase] = bijection(BadInput.schema.addHints(hints), BadInputCase(_), _.badInput)
      val alt = schema.oneOf[CreateTodoError]("BadInput")
    }

    implicit val schema: smithy4s.UnionSchema[CreateTodoError] = union(
      BadInputCase.alt,
    ){
      case c : BadInputCase => BadInputCase.alt(c)
    }
  }
  case class DeleteTodo(input: TodoInput) extends TodoServiceOperation[TodoInput, DeleteTodoError, TodoDeletedCount, Nothing, Nothing]
  object DeleteTodo extends smithy4s.Endpoint[TodoServiceOperation, TodoInput, DeleteTodoError, TodoDeletedCount, Nothing, Nothing] with smithy4s.Errorable[DeleteTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "DeleteTodo")
    val input: smithy4s.Schema[TodoInput] = TodoInput.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[TodoDeletedCount] = TodoDeletedCount.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("DELETE"), smithy.api.NonEmptyString("/todo/{id}"), None),
    )
    def wrap(input: TodoInput) = DeleteTodo(input)
    override val errorable: Option[smithy4s.Errorable[DeleteTodoError]] = Some(this)
    val error: smithy4s.UnionSchema[DeleteTodoError] = DeleteTodoError.schema
    def liftError(throwable: Throwable) : Option[DeleteTodoError] = throwable match {
      case e: BadInput => Some(DeleteTodoError.BadInputCase(e))
      case _ => None
    }
    def unliftError(e: DeleteTodoError) : Throwable = e match {
      case DeleteTodoError.BadInputCase(e) => e
    }
  }
  sealed trait DeleteTodoError extends scala.Product with scala.Serializable
  object DeleteTodoError extends smithy4s.ShapeTag.Companion[DeleteTodoError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "DeleteTodoError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class BadInputCase(badInput: BadInput) extends DeleteTodoError

    object BadInputCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[BadInputCase] = bijection(BadInput.schema.addHints(hints), BadInputCase(_), _.badInput)
      val alt = schema.oneOf[DeleteTodoError]("BadInput")
    }

    implicit val schema: smithy4s.UnionSchema[DeleteTodoError] = union(
      BadInputCase.alt,
    ){
      case c : BadInputCase => BadInputCase.alt(c)
    }
  }
}

sealed trait TodoServiceOperation[Input, Err, Output, StreamedInput, StreamedOutput]
