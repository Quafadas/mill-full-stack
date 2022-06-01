import { spawnSync } from "child_process";
import { defineConfig } from "vite";

const alias = isDev()
  ? runMillCommand("frontend.publicDev")
  : runMillCommand("frontend.publicProd");

export default defineConfig({
  resolve: {
    alias: alias,
  },
});

function isDev() {
  return process.env.NODE_ENV !== "production";
}

function runMillCommand(command) {
  const result = spawnSync("./mill", ["show", command], {
    stdio: [
      "pipe", // StdIn.
      "pipe", // StdOut.
      "inherit", // StdErr.
    ],
  });

  return JSON.parse(result.stdout);
}