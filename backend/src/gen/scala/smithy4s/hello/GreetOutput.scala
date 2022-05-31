package smithy4s.hello

import smithy4s.schema.Schema._

case class GreetOutput(message: Option[String] = None)
object GreetOutput extends smithy4s.ShapeTag.Companion[GreetOutput] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "GreetOutput")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[GreetOutput] = struct(
    string.optional[GreetOutput]("message", _.message),
  ){
    GreetOutput.apply
  }.withId(id).addHints(hints)
}