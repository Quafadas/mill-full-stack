devBack:
  mill -j 0 -w backend.runBackground

devFront:
  cs launch io.github.quafadas::sjsls:0.2.0 -- \
    --path-to-index-html /Users/simon/Code/mill-full-stack/mill-full-stack/frontend/ui \
    --build-tool mill \
    --mill-module-name frontend  \
    --port 3002 \
    --out-dir /Users/simon/Code/mill-full-stack/mill-full-stack/out/frontend/fastLinkJS.dest \
    --proxy-prefix-path /api \
    --proxy-target-port 8080 \
    --extra-build-args -j --extra-build-args 0 \
    --client-routes-prefix /app

compileAll:
  mill __.compile -j 0

checkUpdates:
  mill mill.scalalib.Dependency/showUpdates

getDeps:
  mill __.prepareOffline

setupIde:
  mill --import ivy:com.lihaoyi::mill-contrib-bloop:  mill.contrib.bloop.Bloop/install

generateSmithyConfig:
   mill smithy4s.codegen.LSP/updateConfig