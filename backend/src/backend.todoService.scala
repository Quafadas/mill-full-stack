package hello

import hello.*
import cats.effect.IO
import cats.syntax.all.*
import scala.collection.mutable.ListBuffer

object TodoImpl extends TodoService[IO]:

  extension (s: String) def todoId = TodoId(s)

  val startList = List(
    "Check laminar docs",
    "Check sap ui5 scala docs",
    "Check less docs",
    "Check vite.config.js for proxy config to backend",
    "check smithy4s docs",
    "check http4s docs",
    "check mill docs",
    "Google jobby tutorial for better advice including but not limited to database",
    "check skunk docs",
    "profit"
  )

  val todoDB: ListBuffer[Todo] = ListBuffer.from(
    startList.zipWithIndex.map { case (s, idx) =>
      Todo(s"$idx".todoId, false, s.some)
    }
  )

  def getTodo(id: String) = IO {
    todoDB.find(_.id.value == id).getOrElse(throw new Exception(s"Todo with $id not found"))
  }

  def getTodos() =
    scribe.cats[IO].info("here") >>
      IO.pure(Todos(todoDB.toList))

  def createTodo(complete: Boolean, description: Option[String]) =
    val newID = java.util.UUID.randomUUID.toString
    val newT = Todo(id = newID.todoId, complete = complete, description)
    todoDB.addOne(newT)
    IO.pure(newT)
  end createTodo

  def updateTodo(id: TodoId, complete: Boolean, description: Option[String]) =
    scribe.info("update todo")
    val newT = Todo(id = id, complete = complete, description = description)
    todoDB.find(_.id == id) match
      case Some(t) =>
        todoDB -= t
        todoDB += newT
        IO.pure(newT)
      case None => throw new Exception(s"Todo with $id not found")
    end match
  end updateTodo

  def deleteTodo(id: String): IO[TodoDeleted] =
    scribe.info(s"delete $id")
    val toDel = todoDB.find(_.id.value == id)
    toDel match
      case Some(t) =>
        todoDB -= t
        IO.pure(TodoDeleted(id.todoId))
      case None => throw new Exception(s"Todo with $id not found")
    end match
  end deleteTodo
end TodoImpl
