package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.{*, given}
import cats.syntax.option.*

import io.circe.{Encoder, Decoder}
import io.circe.syntax.*
import org.scalajs.dom
import viz.vega.plots.BarChart
import org.scalajs.dom.html.Div
import java.util.UUID
import scala.scalajs.js.annotation.JSExportTopLevel

import smithy4s.http4s.*
import org.scalajs.dom.*
import org.http4s.dom.FetchClientBuilder
import cats.effect.*
import hello.HelloWorldService
import hello.GreetOutput
import cats.effect.unsafe.implicits.global

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import hello.TodoClient
import hello.Clients
import hello.TodoService
import hello.Todo

import com.raquo.waypoint.Router
import java.awt.Checkbox
import be.doeraene.webcomponents.ui5.configkeys.IconName.strikethrough
import hello.TodoId
import scala.concurrent.duration.Duration.apply
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import com.raquo.laminar.nodes.ReactiveHtmlElement

def io2Es[A](in: IO[A]): EventStream[A] = EventStream.fromFuture(in.unsafeToFuture())

