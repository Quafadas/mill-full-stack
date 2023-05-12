package hello

import hello._
import cats.effect.IO
import cats.syntax.all._
import scala.collection.mutable.ListBuffer

object TodoImpl extends TodoService[IO] {

    extension (s:String)
        def todoId = TodoId(s)

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

    val todoDB : ListBuffer[Todo] = ListBuffer.from(
        startList.zipWithIndex.map{case(s, idx) =>
            Todo(s"$idx".todoId, false, s.some)
        }
    )

    def getTodo(id: String) = IO{
        todoDB.find(_.id.value == id).getOrElse(throw new Exception(s"Todo with $id not found"))
    }

    def getTodos() = 
        scribe.cats[IO].info("here") >>
        IO.pure{Todos(todoDB.toList)}

    def createTodo( complete: Boolean, description: Option[String]) =
        val newID = java.util.UUID.randomUUID.toString 
        val newT = Todo(id = newID.todoId, complete = complete, description )
        todoDB.addOne(newT)
        IO.pure(newT)

    def updateTodo(id: TodoId, complete: Boolean, description: Option[String]) = 
        val newT = Todo(id = id, complete = complete, description = description )
        todoDB.find(_.id == id).getOrElse(throw new Exception(s"Todo with $id not found"))
        todoDB.dropWhileInPlace(_.id == id)
        todoDB.addOne(newT)
        IO.pure(newT)

    def deleteTodo(id: String) = 
        scribe.info(s"delete $id")
        val count = todoDB.count(_.id == id.todoId)
        todoDB.dropWhileInPlace(_.id == id.todoId)
        IO.pure(TodoDeleted(id.todoId))

}
