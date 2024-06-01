devFront:
  cs launch io.github.quafadas:live-server-scala-cli-js_3:0.0.21 -- --path-to-index-html /Users/simon/Code/mill-full-stack/mill-full-stack/frontend/ui --build-tool mill --mill-module-name frontend --port 3001 --out-dir /Users/simon/Code/mill-full-stack/mill-full-stack/out/frontend/fastLinkJS.dest --client-routes-prefix /app

devWithProxy:
  cs launch io.github.quafadas:live-server-scala-cli-js_3:0.0.21 -- --path-to-index-html /Users/simon/Code/mill-full-stack/mill-full-stack/frontend/ui --build-tool mill --mill-module-name frontend --port 3001 --out-dir /Users/simon/Code/mill-full-stack/mill-full-stack/out/frontend/fastLinkJS.dest --client-routes-prefix /app --proxy-prefix-path /api --proxy-target-port 8080

devServerHelp:
  cs launch io.github.quafadas:live-server-scala-cli-js_3:0.0.21 -- --help