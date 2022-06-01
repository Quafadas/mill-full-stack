package smithy4s.hello

import smithy4s.schema.Schema._

case class Person(name: String, town: Option[String] = None)
object Person extends smithy4s.ShapeTag.Companion[Person] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.hello", "Person")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Person] = struct(
    string.required[Person]("name", _.name).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
    string.optional[Person]("town", _.town).addHints(smithy.api.HttpQuery("town")),
  ){
    Person.apply
  }.withId(id).addHints(hints)
}