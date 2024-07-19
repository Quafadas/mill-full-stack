FROM azul/zulu-openjdk-alpine:17

# See the GHA for building the assembly
COPY "./out/backend/assembly.dest/out.jar" "/app/app.jar"

EXPOSE $PORT

ENTRYPOINT [ "java", "-Dport=8080", "-DisProd=true", "-jar", "/app/app.jar" ]