package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core._
import cats.syntax.option.*

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

import com.raquo.waypoint.Router
import java.awt.Checkbox
import be.doeraene.webcomponents.ui5.configkeys.IconName.strikethrough
import hello.TodoId
import scala.concurrent.duration.Duration.apply
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import hello.Todos
import javax.print.attribute.standard.DialogTypeSelection



object HomePage {

  val todoList = Var[List[Todo]](List()) 
  val removeTodoBus = EventBus[String]()
  val removeTodo = removeTodoBus.events.map(id => todoList.update(curr => curr.filter(_.id.value != id)))

  lazy val errorBus: EventBus[Throwable] = new EventBus[Throwable]

  def render()(using api: Api, router: Router[Pages]) =    
    div(
      errorBus --> Observer[Throwable] { err =>
        scribe.error(err)
      },
      Dialog(
        inContext(el => errorBus.events.mapTo(()) --> Observer[Unit](_ => el.ref.show())),
        _.state := ValueState.Error ,
        "An error has occured, it has been recorded in the console log. Your interactions with this page may have been lost. Please refresh the page (press f5). If this error persists, fetch help... "
      ),
      cls := "page-container",
      api.stream(_.todo.getTodos()) --> todoList.writer.contramap[Todos](_.todos),
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
          cls := "inner-home-container",
          p(
            span("An example of a todo app. The list below is unordered by fabulousness."),
            ul(
              li(
                Link("Laminar", _.href := "https://laminar.dev")
              ),
              li(
                Link("SAP UI5 bindings", _.href := "https://sherpal.github.io/laminar-ui5-demo/?componentName=")
              ),
              li(
                Link("Less", _.href := "https://lesscss.org/#")
              ),
              li(
                Link("Smithy4s", _.href := "https://disneystreaming.github.io/smithy4s/docs/overview/intro")
              )
            ),
            span("A template based almost entirely on aggressive plagarisation of the excellent ideas of others. ")
          ),
          renderDataTable(),
          p(
            child.text <-- todoList.signal.map(_.mkString(","))
          )
        )
      )
    )

  def renderDataTable()(using api: Api) =
    div(
      cls := "todo-table-container",
      table(
        cls := "todo-table",
        thead(
          cls := "todo-table-header",
          tr(
            th("Id hi"),
            th("Description"),
            th("Done"),
            th(""),
            th(
              span(
                linkIcon(IconName.add, addAction)
              )
            )
          )
        ),
        children <-- todoList.signal.split(_.id.value)(renderTodo)
      )
    )

  def renderTodo(id: String, initialTodo: Todo, todoS: Signal[Todo])(using api: Api) =
    val isEditing = Var(false)
    val editedValue = Var[String]("")

    tr(
      td(
        cls := "bigText",
        id
      ),
      td(
        Input(
          width := "35vw",
          _.readonly <-- isEditing.signal.map(!_),
          _.events.onChange.mapToValue --> editedValue.writer,
          value <-- todoS.map(_.description.getOrElse(""))
        )
      ),
      td(
        cls := "centered",
        CheckBox(
          _.checked <-- todoS.map(_.complete)
        )
      ),
      td(
        th(
          isEditing.signal.childWhenTrue(
            div(
              linkIcon(
                IconName.save
              )
              // onClick --> saveNew value
            )
          ),
          isEditing.signal.childWhenFalse(
            linkIcon(
              IconName.edit
            )
          ),
          onClick --> isEditing.updater((cur, _) => !cur)
          // child.text <-- isEditing
        )
      ),
      td(
        th(
          Link(
            Icon(
              _.name := IconName.delete,
              width := "24px",
              height := "24px"
            ),
            onClick.mapTo(id) --> deleteAction
          )
        )
      )
    )

  def deleteAction(using api: Api) =
    Observer[String] { s =>
      api.stream(_.todo.deleteTodo(s))
    }

  def addAction(using api: Api) =
    Observer[Unit] { s =>
      api.stream(_.todo.createTodo(false, None))
    }

  def todoListRemove(id: TodoId) =
    todoList.update(currentList => currentList.filter(_.id != id))

  def todoListAdd(newTodo: Todo) =
    todoList.update(currentList => currentList :+ newTodo)
}

