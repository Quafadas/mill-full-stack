namespace smithy4s.hello

use smithy4s.api#simpleRestJson

@simpleRestJson
service TodoService {
  version: "1.0.0",
  operations: [GetTodo, GetTodos],
  errors: [BadInput]
}

@readonly
@http(method: "GET", uri: "/todo")
operation GetTodos {   
  output: Todos,  
}

@readonly
@http(method: "GET", uri: "/todo/{id}")
operation GetTodo {
  input: TodoInput,
  output: Todo,
  errors: [BadInput]
}

structure TodoInput {
  @httpLabel
  @required
  id:String
}

@error("client")
@httpError(480)
structure BadInput {  
  message: String
}

structure Todo {
  @required
  id: String,
  description: String,
  complete: Boolean
}

structure Todos {
  todos: TodoList
}


list TodoList {
  member: Todo
}