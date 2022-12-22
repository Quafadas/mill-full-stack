package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core._

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


def io2Es[A](in: IO[A]): EventStream[A] = EventStream.fromFuture(in.unsafeToFuture())

object Main {

  @JSExportTopLevel("main")
  def main(): Unit = {
    renderOnDomContentLoaded(dom.document.querySelector("#app"), app(using AppPage.router))
  }

  def app(using router: Router[AppPage]) =
    div(
      child <-- AppPage.renderPage
    )
}

object HomePage {


  val helloClient: org.http4s.client.Client[IO] = FetchClientBuilder[IO].create
  val myClient: Resource[cats.effect.IO, TodoService[cats.effect.IO]] = Clients.todoClient(helloClient)

  val todoList = Var[List[Todo]](List())
  val getTodos = io2Es(myClient.use(_.getTodos()))
  def deleteAction(id:String) =     
    Observer[Unit]{ _ =>
      scribe.info("here")
      io2Es(myClient.use( c => c.deleteTodo(id) ))
    }

  def render() =
    div(
      cls := "page-container",
      getTodos.map(_.todos.get) --> todoList.writer,
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

  def renderDataTable() =
    div(
      cls := "todo-table-container",
      table(
        cls := "todo-table",
        thead(
          cls := "todo-table-header",
          tr(
            th("Id"),
            th("Description"),
            th("Done"),
            th(""),
            th(
              span(
                linkIcon(IconName.add)
              )
            )
          )
        ),
        children <-- todoList.signal.split(_.id.value)(renderTodo)
      )
    )

  def renderTodo(id: String, initialTodo: Todo, todoS: Signal[Todo]) =
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
          onClick --> isEditing.updater((cur, _) => !cur),          
          // child.text <-- isEditing
        )
      ),
      td(        
        th(
          linkIcon(
            IconName.delete,
            deleteAction(id)
          )
        )
      )
    )



}

def linkIcon(iconName: IconName, doSomething: Observer[Unit] = Observer[Unit]{Unit => ()} ) =
  Link(
    Icon(
      _.name := iconName,
      width := "24px",
      height := "24px"
    ),
    onClick.mapToUnit --> doSomething
  )


