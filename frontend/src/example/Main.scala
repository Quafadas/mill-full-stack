package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

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
import smithy4s.hello.HelloWorldService
import smithy4s.hello.GreetOutput
import cats.effect.unsafe.implicits.global

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main {

  case class DataItem(
      category: String,
      amount: Double,
      id: UUID = UUID.randomUUID()
  ) derives Encoder.AsObject, Decoder

  object DataItem {
    def apply(): DataItem = DataItem(scala.util.Random.nextString(5), Math.random())
  }

  def io2Es[A](in : IO[A]) : EventStream[A] = EventStream.fromFuture(in.unsafeToFuture())

  val helloClient: org.http4s.client.Client[IO] = FetchClientBuilder[IO].create
  val myClient: Resource[cats.effect.IO, HelloWorldService[cats.effect.IO]] = example.MyClient.helloWorldClient(helloClient)
  

  val dataVar = Var[List[DataItem]](List(DataItem("one", 1.2)))
  val dataSignal = dataVar.signal
  val allValues = dataSignal.map(_.map(_.amount))

  @JSExportTopLevel("main")
  def main(): Unit = {
    renderOnDomContentLoaded(dom.document.querySelector("#app"), appElement())
  }

  def appElement() = {    
    div(
      h1("Hello Vite!"),
      div(
        child.text <-- {
            io2Es(myClient.use(_.greet("Smithy4s")).map(_.message.getOrElse("nothing ")) )
          }                        
      ),
      renderDataTable(),
      ul(
        li("Sum of values: ", child.text <-- allValues.map(_.sum)),
        li(
          "Average value: ",
          child.text <-- allValues.map(vs => vs.sum / vs.size)
        )
      ),
      child <-- dataSignal.map { ds => 
        val json = ds.asJson       
        div(
          height :="40vmin", width := "40vmin", idAttr := "viz",
          onMountCallback { nodeCtx =>
            val plotDiv = nodeCtx.thisNode.ref.asInstanceOf[viz.PlotTarget]
            BarChart(
              List(
                viz.Utils.fillDiv,
                spec => spec("data")(0)("values") = ds.u
              )
            )(using plotDiv)
          }
        )
      }
    )
  }

  def renderDataTable() = {
    table(
      thead(
        tr(th("Label"), th("Value"), th("Action"))
      ),
      tbody(
        children <-- dataSignal.split(_.id) { (id, initial, itemSignal) =>
          renderDataItem(id, itemSignal)
        }
      ),
      tfoot(
        tr(
          td(
            button(
              "???",
              onClick --> (_ => dataVar.update(data => data :+ DataItem()))
            )
          )
        )
      )
    )
  }

  def renderDataItem(id: UUID, item: Signal[DataItem]) = {
    val labelUpdater = dataVar.updater[String] { (data, newLabel) =>
      data.map(item =>
        if item.id == id then item.copy(category = newLabel) else item
      )
    }

    val valueUpdater = dataVar.updater[Double] { (data, newValue) =>
      data.map(item =>
        if item.id == id then item.copy(amount = newValue) else item
      )
    }

    tr(
      td(inputForString(item.map(_.category), labelUpdater)),
      td(inputForDouble(item.map(_.amount), valueUpdater)),
      td(
        button(
          "???????",
          onClick --> (_ => dataVar.update(data => data.filter(_.id != id)))
        )
      )
    )
  }

  def inputForString(
      valueSignal: Signal[String],
      valueUpdater: Observer[String]
  ): Input = {
    input(
      typ := "text",
      controlled(
        value <-- valueSignal,
        onInput.mapToValue --> valueUpdater
      )
    )
  }

  def inputForDouble(
      valueSignal: Signal[Double],
      valueUpdater: Observer[Double]
  ): Input = {
    val strValue = Var[String]("")
    input(
      typ := "text",
      controlled(
        value <-- strValue.signal,
        onInput.mapToValue --> strValue
      ),
      valueSignal --> strValue.updater[Double] { (prevStr, newValue) =>
        if prevStr.toDoubleOption.contains(newValue) then prevStr
        else newValue.toString
      },
      strValue.signal --> { valueStr =>
        valueStr.toDoubleOption.foreach(valueUpdater.onNext)
      }
    )
  }
}