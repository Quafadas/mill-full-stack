package smithy4s.hello

import smithy4s.schema.Schema._

case class BadInput(message: Option[String] = None) extends Throwable {
  override def getMessage() : String = message.orNull
}
object BadInput extends smithy4s.ShapeTag.Companion[BadInput] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "BadInput")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.CLIENT,
    smithy.api.HttpError(480),
  )

  implicit val schema: smithy4s.Schema[BadInput] = struct(
    string.optional[BadInput]("message", _.message).addHints(smithy.api.JsonName("error")),
  ){
    BadInput.apply
  }.withId(id).addHints(hints)
}