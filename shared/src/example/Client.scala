package example

import smithy4s.http4s._
import org.http4s.Uri
import org.http4s.client.Client
import cats.effect.IO
import cats.effect.Resource

//import smithy4s.hello.HelloWorldService

// object MyClient {
//     //bah
//   def helloWorldClient(
//       http4sClient: Client[IO]
//   ): Resource[IO, HelloWorldService[IO]] =
//     HelloWorldService.simpleRestJson.clientResource(
//       http4sClient,
//       Uri.unsafeFromString("http://localhost:8080")
//     )
// }
