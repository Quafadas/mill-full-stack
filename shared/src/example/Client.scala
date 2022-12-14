package hello

import smithy4s.http4s._
import org.http4s.Uri
import org.http4s.client.Client
import cats.effect.IO
import cats.effect.Resource

import hello.HelloWorldService
import hello.TodoService

object Clients {    
  def helloWorldClient(
      http4sClient: Client[IO]
  ): Resource[IO, HelloWorldService[IO]] =
    SimpleRestJsonBuilder
        .apply(HelloWorldService)        
        .client(http4sClient)
        .uri(Uri.unsafeFromString("/"))
        .resource

  def todoClient(
      http4sClient: Client[IO]
  ): Resource[IO, TodoService[IO]] =
    SimpleRestJsonBuilder
        .apply(TodoService)
        .client(http4sClient)
        .uri(Uri.unsafeFromString("/"))
        .resource
}

object TodoClient {    

}

