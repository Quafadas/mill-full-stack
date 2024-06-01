# Goal
Ride the openAI hypetrain in a manner that integrates with an existing body of work in scala/JS.

Concretely, I'd like to build a chatbot, which drives the trivial laminar / smithy specified "todo" app in this repo. I believe, that a successful POC here, would scale to other, more interesting usecases.

# Solution Sketch
A significant body of LLM work, appears to be taking place in python (duh). Cool, but relatively tricky for me to leverage or integrate with my own, existing tail of work.

https://github.com/hwchase17/langchainjs however, looks like it offers many of the building blocks that we would need. It also has a long term commitment to feature parity with the python library.

Finally, it has an explicit browser target. Given the existence of scala JS, this looks like a promising starting point, if they can be made to play nicely together.

## Attack angle 1
Point scalably typed at langchainJS. This "works". In fact, it's startlingly quick to knock out a crappy laminar driven chatbot. However, it's entirely vanilla flavoured. For this to be useful, we'd need to customise an agent / conversation specific to our (currently simple) todo-app needs.

Unhappily, beyond this initial vanilla-bot point, our easy progress and fast-fortune so far breaks down.

### Problem 1
https://stackoverflow.com/questions/76238878/scala-js-scalable-typed-vite

As far as I can tell, at some point, something about the ST <-> SJS <-> vite import analysis breaks down. Uncommenting this line, gets me an import error in vite. Frustratingly, I can follow the breadcrumbs back through the generated JS, through the typescript, and back into the library itself in the IDE.
https://github.com/Quafadas/mill-full-stack/blob/ff0730efb96c047c9a9935494bbb2108beb5e423/frontend/src/chat.page.scala#L90

The files all appear to be in the right place, but vite don't believe that. After posting up on a few places... I conclude this problem, is beyond me to solve.

#### "Solution" 1


I believe the canonical way to tell bot about our data, would be by subclassing "tool".
https://js.langchain.com/docs/modules/agents/tools/

This form, of integration, is not (really) what scalably typed was designed for.
https://github.com/ScalablyTyped/Converter/issues/535

Give up for now






