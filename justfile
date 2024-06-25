devFront:
  cs launch io.github.quafadas:live-server-scala-cli-js_3:0.1.3 -- --path-to-index-html /Users/simon/Code/mill-full-stack/mill-full-stack/frontend/ui --build-tool mill --mill-module-name frontend --port 3001 --out-dir /Users/simon/Code/mill-full-stack/mill-full-stack/out/frontend/fastLinkJS.dest --client-routes-prefix /app

dev:
  cs launch io.github.quafadas:live-server-scala-cli-js_3:0.1.3 -- \
    --path-to-index-html /Users/simon/Code/mill-full-stack/mill-full-stack/frontend/ui \
    --build-tool mill \
    --mill-module-name frontend  \
    --port 3001 \
    --out-dir /Users/simon/Code/mill-full-stack/mill-full-stack/out/frontend/fastLinkJS.dest \
    --proxy-prefix-path /api \
    --proxy-target-port 8080

devServerHelp:
  cs launch io.github.quafadas:live-server-scala-cli-js_3:0.1.3 -- --help

compileAll:
  mill __.compile -j 0

checkUpdates:
  mill mill.scalalib.Dependency/showUpdates

setupIde:
  mill --import ivy:com.lihaoyi::mill-contrib-bloop:  mill.contrib.bloop.Bloop/install