package frontend

import com.raquo.laminar.api.L.EventStream
import urldsl.vocabulary.{Segment, Param, UrlMatching}
import urldsl.language.PathSegment
import urldsl.errors.DummyError
import hello.TodoService
import cats.effect.IO
import scala.concurrent.Future
import cats.effect.unsafe.implicits.global
import org.http4s.Uri
import org.http4s.dom.FetchClientBuilder
import hello.TodoClient
import scala.concurrent.ExecutionContextExecutor

implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

class Api private (
    val todo: TodoService[IO]
): 
  def future[A](a: Api => IO[A]): Future[A] =
    a(this).unsafeToFuture()

  def stream[A](a: Api => IO[A]): EventStream[A] =
    EventStream.fromFuture(future(a))

end Api

object Api:
  def create() =
    val uri = Uri.unsafeFromString("/")

    val client = FetchClientBuilder[IO].create

    val todo = TodoClient.todoClient(client)
    
    Api(todo)
  end create
end Api
