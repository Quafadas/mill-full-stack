# mill-full-stack

An experiment in full stack with mill, smithy, scalably typed... 

Basically, "all the things" :-)!

# Development

Start 3 consoles

1. ./mill -w backend.runBackground
2. ./mill -j 0 -w frontend.publicDev
3. npm run dev

These three things recompile and restart the backend. Continuously recompile scala js. Start vite, which is configured to take advantage of your UI. 

# Notable configuration

1. No authentication
2. CORS disabled
3. backend.assembly should generate a JAR which contains the entire application (inc. frontend)


The first compilation will like be rather slow. Incremental change has been for me, startlingly quick when one appreciates that the entire HTTP api, is statically typed.

# Library choices

1. smithy4s
2. laminar

Those two choices basically dicatate most of the other parts of build.sc. The frontend is driven through [vite](https://vitejs.dev).

# Handy references
https://softwaremill.com/practical-guide-to-error-handling-in-scala-cats-and-cats-effect/



smithy4s can load smithy models from jars, so you can share smithy files across several codebases 
https://github.com/disneystreaming/smithy4s/tree/main/modules/protocol/resources/META-INF/smithy
https://github.com/disneystreaming/smithy4s/blob/main/modules/codegen-plugin/src/sbt-test/codegen-plugin/dependencies-only/build.sbt#L9
