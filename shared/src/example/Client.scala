package hello

import smithy4s.http4s._
import org.http4s.Uri
import org.http4s.client.Client
import cats.effect.IO
import cats.effect.Resource

import hello.HelloWorldService

object MyClient {    
  def helloWorldClient(
      http4sClient: Client[IO]
  ): Resource[IO, HelloWorldService[IO]] =
    SimpleRestJsonBuilder
        .apply(HelloWorldService)        
        .client(http4sClient)
        .uri(Uri.unsafeFromString("http://localhost:8080"))
        .resource
}
