package hello

import cats.effect.IO
import smithy4s.http4s.SimpleRestJsonBuilder
import shared.HelloWorldService
import shared.TodoService
import smithy4s.http4s.swagger.docs
import cats.syntax.all.*
import cats.data.Kleisli
import smithy4s.UnsupportedProtocolError
import org.http4s.HttpRoutes

import cats.effect.*
import cats.implicits.*

def makeRoutes: Either[UnsupportedProtocolError, HttpRoutes[IO]] =

    val allFrontendRoutes = io.github.quafadas.sjsls.defaultFrontendRoutes[IO]()
    val helloRoutes = SimpleRestJsonBuilder.routes(HelloWorldImpl).make
    val todoRoutes = SimpleRestJsonBuilder.routes(TodoImpl).make
    val docHelloRoutes = docs[IO](HelloWorldService)
    val docTodoRoutes = docs[IO](TodoService)

    val apiRoutes = (todoRoutes, helloRoutes).mapN(_ <+> _)
    apiRoutes.map(api => api <+> docTodoRoutes <+> docHelloRoutes <+> allFrontendRoutes)