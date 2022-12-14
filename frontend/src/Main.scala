package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.{*, given}

import io.circe.{Encoder, Decoder}
import io.circe.syntax._
import viz.dsl.Conversion.u
import org.scalajs.dom
import viz.vega.plots.BarChart
import org.scalajs.dom.html.Div
import java.util.UUID
import scala.scalajs.js.annotation.JSExportTopLevel

import smithy4s.http4s._
import org.scalajs.dom._
import org.http4s.dom.FetchClientBuilder
import cats.effect._
import hello.HelloWorldService
import hello.GreetOutput
import cats.effect.unsafe.implicits.global

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import hello.TodoClient
import hello.Clients
import hello.TodoService
import hello.Todo

object Main {

  def io2Es[A](in: IO[A]): EventStream[A] = EventStream.fromFuture(in.unsafeToFuture())

  val helloClient: org.http4s.client.Client[IO] = FetchClientBuilder[IO].create
  val myClient: Resource[cats.effect.IO, TodoService[cats.effect.IO]] = Clients.todoClient(helloClient)

  @JSExportTopLevel("main")
  def main(): Unit = {
    renderOnDomContentLoaded(dom.document.querySelector("#app"), appElement())
  }

  def appElement() = {

    val todoList = Var[List[Todo]](List())
    val getTodos = myClient.use(_.getTodos())
    div(
      io2Es(getTodos.map(_.todos.get)) --> todoList.writer,
      Page(
        width := "100vw",
        height := "100vh",
        _.slots.header := Bar(
          _.design := BarDesign.Header,
          // _.slots.startContent := Button(_.tooltip := "Go Home", _.icon := IconName.home),
          // _.slots.endContent := Button(_.tooltip := "Settings", _.icon := IconName.`action-settings`),
          Title(_.level := TitleLevel.H1, "To Do App")
        ),        
        div(          
          p(
            child.text <-- todoList.signal.map(_.mkString(","))
          )
        )
      )
    )
  }
}
