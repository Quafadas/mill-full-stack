package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core.*
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
import hello.Todos
import javax.print.attribute.standard.DialogTypeSelection
import typings.marked.markedStrings.del

object HomePage:

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
        _.state := ValueState.Error,
        "An error has occured, it has been recorded in the console log. Your interactions with this page may have been lost. Please refresh the page (press f5). If this error persists, fetch help... "
      ),
      cls := "page-container",
      api.stream(_.todo.getTodos()) --> todoList.writer.contramap[Todos](_.todos),
      Page(
        width := "100vw",
        height := "100vh",
        // _.slots.header := Bar(
        //   // _.design := BarDesign.Header,
        //   // _.slots.startContent := Button(_.tooltip := "Go Home", _.icon := IconName.home),
        //   // _.slots.endContent := Button(_.tooltip := "Settings", _.icon := IconName.`action-settings`),
        //   "To Do App"
        // ),
        div(
          cls := "inner-home-container",
          p(
            span(
              "An example of a todo app built on wonderful scala libraries. The list below is unordered by fabulousness."
            ),
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
                linkIcon(IconName.add),
                onClick.mapToUnit --> addAction
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
    val clickObserver = Observer[dom.MouseEvent](onNext = ev => dom.console.log(ev.screenX))

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
        child <-- todoS.map(t =>
          val d = t.description
          CheckBox(
            _.checked := t.complete,
            _.events.onChange.mapToChecked --> Observer[Boolean] { checked =>
              api.stream(
                _.todo
                  .updateTodo(
                    TodoId(id),
                    checked,
                    d
                  )
                  .map(updated => updateTodo(updated))
              )
            }
          )
        )
      ),
      td(
        th(
          isEditing.signal.childWhenTrue(
            div(
              onClick.mapTo((id, editedValue.now())) --> Observer[(String, String)] { case (id, editedValue) =>
                api.stream(
                  _.todo.updateTodo(
                    TodoId(id),
                    false,
                    editedValue.some
                  ).map(updated => updateTodo(updated))
                )
              },
              linkIcon(
                IconName.save
              )
            )
          ),
          isEditing.signal.childWhenFalse(
            linkIcon(
              IconName.edit
            )
          ),
          onClick --> isEditing.updater((cur, _) => !cur)
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
  end renderTodo

  def deleteAction(using api: Api) =
    Observer[String] { s =>
      api.stream(_.todo.deleteTodo(s).map(del => todoListRemove(del.id)))
    }

  def addAction(using api: Api) =
    Observer[Unit] { s =>
      api.stream(_.todo.createTodo(false, None).map(tnew => todoListAdd(tnew)))
    }

  def todoListRemove(id: TodoId) =
    todoList.update(currentList => currentList.filter(_.id != id))
  end todoListRemove

  def updateTodo(t: Todo) =
    todoList.update(currentList => currentList.map(tcur => if (tcur.id == t.id) t else tcur))
  end updateTodo

  def todoListAdd(newTodo: Todo) =
    todoList.update(currentList => currentList :+ newTodo)
  end todoListAdd

end HomePage
