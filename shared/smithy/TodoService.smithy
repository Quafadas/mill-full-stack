namespace hello

use alloy#simpleRestJson

@simpleRestJson
service TodoService {
  version: "1.0.0",
  operations: [GetTodo, GetTodos, UpdateTodo, CreateTodo, DeleteTodo],
  errors: [BadInput]
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
  output: Todo,
  errors: [BadInput]
}

@http(method: "POST", uri: "/api/todo/{id}")
operation UpdateTodo {    
  input: Todo, 
  output: Todo,
  errors: [BadInput]
}

@idempotent
@http(method: "PUT", uri: "/api/todo")
operation CreateTodo {    
  input: NewTodo, 
  output: Todo,
  errors: [BadInput]
}

@idempotent
@http(method: "DELETE", uri: "/api/todo/{id}")
operation DeleteTodo {    
  input: TodoInput, 
  output: TodoDeletedCount,
  errors: [BadInput]
}

structure TodoInput {
  @httpLabel
  @required
  id:String
}

structure TodoDeletedCount {
  @httpLabel
  @required
  count: Integer
}


@error("client")
@httpError(480)
structure BadInput {
  @jsonName("error")
  message: String
}

structure NewTodo {
  description: String,
  @required
  complete: Boolean
}

structure Todo {
  @httpLabel
  @required
  id: String,
  description: String,
  @required  
  complete: Boolean
}

structure Todos {
  todos: TodoList
}


list TodoList {
  member: Todo
}