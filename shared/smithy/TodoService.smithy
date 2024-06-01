$version: "2"

namespace frontend

use alloy#simpleRestJson


@trait
structure metadata {
  @required
  description: String
}

@metadata(description: "This is my own integer shape")
string TodoId


@simpleRestJson
service TodoService {
  version: "1.0.0",
  operations: [GetTodo, GetTodos, UpdateTodo, CreateTodo, DeleteTodo]
}

@readonly
@http(method: "GET", uri: "/api/todo")
operation GetTodos {
  output: Todos,
}

@readonly
@http(method: "GET", uri: "/api/todo/{id}")
operation GetTodo {
  input: TodoInput,
  output: Todo
}

@http(method: "POST", uri: "/api/todo/{id}")
operation UpdateTodo {
  input: Todo,
  output: Todo
}

@idempotent
@http(method: "PUT", uri: "/api/todo")
operation CreateTodo {
  input: NewTodo,
  output: Todo
}

@idempotent
@http(method: "DELETE", uri: "/api/todo/{id}")
operation DeleteTodo {
  input: TodoInput,
  output: TodoDeleted
}

structure TodoInput {
  @httpLabel
  @required
  id:String
}

structure TodoDeleted {
  @httpLabel
  @required
  id: TodoId
}


structure NewTodo {
  description: String,
  @required
  complete: Boolean
}

structure Todo {
  @httpLabel
  @required
  id: TodoId,
  description: String,
  @required
  complete: Boolean
}

structure Todos {
  @required
  todos: TodoList
}


list TodoList {
  member: Todo
}