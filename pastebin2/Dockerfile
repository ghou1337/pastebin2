FROM eclipse-temurin:21.0.2_13-jdk AS build

ARG JAR_FILE=target/pastebin-0.0.1-SNAPSHOT.jar
WORKDIR /build

COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract --destination extracted

FROM eclipse-temurin:21.0.2_13-jdk

RUN addgroup spring-boot-group && adduser --ingroup spring-boot-group spring-boot
USER spring-boot:spring-boot-group
VOLUME /tmp
WORKDIR /application

COPY --from=build /build/extracted/dependencies .
COPY --from=build /build/extracted/spring-boot-loader .
COPY --from=build /build/extracted/snapshot-dependencies .
COPY --from=build /build/extracted/application .

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher", "--spring.profiles.active=docker"]