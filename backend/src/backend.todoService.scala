package hello

import hello._
import cats.effect.IO
import cats.syntax.all._
import scala.collection.mutable.ListBuffer

object TodoImpl extends TodoService[IO] {

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
            Todo(s"$idx", s.some, false.some)
        }
    )

    def getTodo(id: String) = IO{
        todoDB.find(_.id == id).getOrElse(throw new BadInput(s"Todo with $id not found".some))
    }

    def getTodos() = 
        scribe.cats[IO].info("here") >>
        IO.pure{Todos(todoDB.toList.some)}

    def createTodo(description: Option[String], complete: Option[Boolean]) =
        val newID = java.util.UUID.randomUUID.toString 
        val newT = Todo(newID, description, complete)
        todoDB.addOne(newT)
        IO.pure(newT)

    def updateTodo(id: String, description: Option[String], complete: Option[Boolean]) = 
        val newT = Todo(id, description, complete)
        todoDB.find(_.id == id).getOrElse(throw new BadInput(s"Todo with $id not found".some))
        todoDB.dropWhileInPlace(_.id == id)
        todoDB.addOne(newT)
        IO.pure(newT)

    def deleteTodo(id: String) =         
        val count = todoDB.count(_.id == id)
        todoDB.dropWhileInPlace(_.id == id)
        IO.pure(TodoDeletedCount(count))

}
