FROM quafadas/scala-mill:latest

RUN java -version

RUN mkdir -p /tmp
WORKDIR /tmp

# If you need to configure anything private for mill, you can do it here
RUN mkdir -p /root/.mill/ammonite
COPY predef.sc /root/.mill/ammonite/predef.sc
COPY predef.sc /root/.mill/ammonite/predefScript.sc

RUN mkdir -p /tmp/project
COPY build.sc /tmp/build.sc
COPY /project/ /tmp/project/
# This should cache the dependancies for the build
RUN /usr/local/bin/mill --no-server backend.prepareOffline

# Our actual project... 
COPY . /tmp
RUN /usr/local/bin/mill --no-server backend.assembly

# To manage fatness, consider using the following:
# FROM eclipse-temurin:17-jre-alpine
FROM eclipse-temurin:17
COPY --from=0 /tmp/out/backend/assembly.dest/out.jar /tmp/out.jar
EXPOSE 8080
CMD ["java", "-jar", "/tmp/out.jar"]


