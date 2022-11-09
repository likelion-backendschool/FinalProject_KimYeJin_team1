FROM eclipse-temurin:17-alpine as builder
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-alpine
WORKDIR application
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]