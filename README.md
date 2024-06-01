# mill-full-stack

An experiment in full stack with mill, smithy, scala js and laminar...

# Development

First time, you might need to do `npm install` etc. After that,

Start 3 consoles

1. ```./mill -w backend.runBackground```
2. ```./mill -w frontend.publicDev```
3. ```npm run dev```

These three things recompile and restart the backend on change, continuously recompile scala js on change, start vite which will continuously re-server the changed frontend and proxy api requests to the backend.

# Notable configuration

1. No authentication
2. CORS disabled
3. backend.assembly should generate a JAR which contains the entire application (inc. frontend)


The first compilation may be rather slow. Incremental change has been for me, startlingly quick when one appreciates that the entire HTTP api, is statically typed.

# Library choices

1. smithy4s
2. laminar

Those two choices basically dictate most of the other parts of build.sc. The frontend is driven through [vite](https://vitejs.dev).

# Handy references
https://softwaremill.com/practical-guide-to-error-handling-in-scala-cats-and-cats-effect/
https://sherpal.github.io/laminar-ui5-demo/

This is a pretty neat set of http4s examples

https://github.com/fancellu/http4s-circe-example

https://laminar.dev/documentation

|   | Read Only  | Read - Write  |
|---|---|---|
Events | EventStream[A] | EventBus[A] |
State | Signal[A] | Var[A] |

## Smithy
smithy4s can load smithy models from jars, so you can share smithy files across several codebases
https://github.com/disneystreaming/smithy4s/tree/main/modules/protocol/resources/META-INF/smithy
https://github.com/disneystreaming/smithy4s/blob/main/modules/codegen-plugin/src/sbt-test/codegen-plugin/dependencies-only/build.sbt#L9

# Getting started.
You may need to find your predefScript.sc file in $HOME/.mill/ammonite and add
```
interp.repositories() ++= Seq(
    coursierapi.MavenRepository.of("https://jitpack.io")
)
```
In order to resolve mill-dotenv which is hosted on jitpack

Hints: for bvrowaser tools
`await (await fetch('<URL>')).json()`