FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/taskboard-api-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /app/data && chown -R 10001:0 /app
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
