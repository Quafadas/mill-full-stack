namespace smithy4s.hello

use smithy4s.api#simpleRestJson

@simpleRestJson
service HelloWorldService {
  version: "1.0.0",
  operations: [Hello, Greet],
  errors: [BadInput]
}

@readonly
@http(method: "GET", uri: "/hello/{name}")
operation Greet {
  input: GreetInput,
  output: GreetOutput,
  errors: [BadInput]
}

@http(method: "POST", uri: "/{name}", code: 200)
operation Hello {
  input: Person,
  output: Greeting
}

structure GreetInput {
  @httpLabel
  @required
  name:String
}

structure GreetOutput {  
  message:String
}

@error("client")
@httpError(480)
structure BadInput {
  @jsonName("oops")
  message: String
}

structure Person {
  @httpLabel
  @required
  name: String,

  @httpQuery("town")
  town: String
}

structure Greeting {
  @required
  message: String
}
