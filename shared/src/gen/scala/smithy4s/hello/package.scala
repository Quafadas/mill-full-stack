package smithy4s

package object hello {
  type HelloWorldService[F[_]] = smithy4s.Monadic[HelloWorldServiceGen, F]
  object HelloWorldService extends smithy4s.Service.Provider[HelloWorldServiceGen, HelloWorldServiceOperation] {
    def apply[F[_]](implicit F: HelloWorldService[F]): F.type = F
    def service : smithy4s.Service[HelloWorldServiceGen, HelloWorldServiceOperation] = HelloWorldServiceGen
    val id: smithy4s.ShapeId = service.id
  }
  type TodoService[F[_]] = smithy4s.Monadic[TodoServiceGen, F]
  object TodoService extends smithy4s.Service.Provider[TodoServiceGen, TodoServiceOperation] {
    def apply[F[_]](implicit F: TodoService[F]): F.type = F
    def service : smithy4s.Service[TodoServiceGen, TodoServiceOperation] = TodoServiceGen
    val id: smithy4s.ShapeId = service.id
  }

  type TodoList = smithy4s.hello.TodoList.Type

}