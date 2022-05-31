package example

import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s._
import cats.effect.IO
import cats.effect.Resource

object Routes {
    
    //val myRoutes : Resource[IO, HttpRoutes[IO]] = 
        

    val HelloRoutes : Resource[IO, HttpRoutes[IO]] = 
        SimpleRestJsonBuilder.routes(HelloWorldImpl).resource

    val TodoRoutes : Resource[IO, HttpRoutes[IO]] = 
        SimpleRestJsonBuilder.routes(TodoImpl).resource

}