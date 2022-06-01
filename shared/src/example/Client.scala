package example

import smithy4s.http4s._
import org.http4s.Uri
import org.http4s.client.Client
import cats.effect.IO
import cats.effect.Resource

import smithy4s.hello.HelloWorldService

/*
import cats.effect._
import org.http4s.ember.client._
import org.http4s.client._
import java.util.concurrent._
import cats.effect.unsafe.implicits.global 

val blockingPool = Executors.newFixedThreadPool(5)
val httpClient: Client[IO] = JavaNetClientBuilder[IO].create
val myClient = example.MyClient.helloWorldClient(httpClient)
myClient.use(_.greet("simon")).unsafeRunSync() 
*/


object MyClient {
    //bah
  def helloWorldClient(
      http4sClient: Client[IO]
  ): Resource[IO, HelloWorldService[IO]] =
    HelloWorldService.simpleRestJson.clientResource(
      http4sClient,
      Uri.unsafeFromString("http://localhost:8080")
    )
}
