FROM azul/zulu-openjdk-alpine:17

# See the GHA for building the assembly
COPY "./out/backend/assembly.dest/out.jar" "/app/app.jar"

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]