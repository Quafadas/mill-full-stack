package shared

import cats.effect.IO
import cats.effect.Resource
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.middleware.RequestLogger
import shared.HelloWorldService
import shared.TodoService
import smithy4s.http4s.*

object Clients:

  val logger =
    RequestLogger(logHeaders = true, logBody = true, logAction = Some(((str: String) => scribe.cats[IO].info(str))))

  def helloWorldClient(
      http4sClient: Client[IO]
  ): Resource[IO, HelloWorldService[IO]] =
    SimpleRestJsonBuilder
      .apply(HelloWorldService)
      .client(logger(http4sClient))
      .uri(Uri.unsafeFromString("/"))
      .resource

  def todoClient(
      http4sClient: Client[IO]
  ): Resource[IO, TodoService[IO]] =
    SimpleRestJsonBuilder
      .apply(TodoService)
      .client(logger(http4sClient))
      .uri(Uri.unsafeFromString("/"))
      .resource
end Clients

object TodoClient:

  def todoClient(
      http4sClient: Client[IO]
  ): TodoService[IO] =
    SimpleRestJsonBuilder
      .apply(TodoService)
      .client(http4sClient)
      .uri(Uri.unsafeFromString("/"))
      .make
      .fold(throw _, identity)
end TodoClient
