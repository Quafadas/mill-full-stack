package example

import smithy4s.hello._
import cats.effect.IO
import cats.syntax.all._
import scala.collection.mutable.ListBuffer

object TodoImpl extends TodoService[IO] {

    val todoDB : ListBuffer[Todo] = ListBuffer(Todo("1", "do something".some, false.some))

    def getTodo(id: String) = IO{
        todoDB.find(_.id == id).getOrElse(throw new BadInput(s"Todo with $id not found".some))
    }

    def getTodos() = IO.pure{
        Todos(todoDB.toList.some)        
    }

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
