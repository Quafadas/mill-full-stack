package frontend

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.raquo.laminar.api.L.EventStream
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.dom.FetchClientBuilder
import shared.*

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future



class Api private (
    val todo: TodoService[IO]
):
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def future[A](a: Api => IO[A]): Future[A] =
    a(this).unsafeToFuture()

  def stream[A](a: Api => IO[A]): EventStream[A] =
    EventStream.fromFuture(future(a))

end Api

object Api:
  def create() =
    Uri.unsafeFromString("/")

    val client = FetchClientBuilder[IO].create

    val frontendClient = Client[IO] { req =>
      val amendedUri = req.uri.copy(scheme = None, authority = None)
      println(amendedUri)
      val amendedRequest = req.withUri(amendedUri)
      println(amendedRequest)
      client.run(amendedRequest)
    }

    val todo = TodoClient.todoClient(frontendClient)

    Api(todo)
  end create
end Api
