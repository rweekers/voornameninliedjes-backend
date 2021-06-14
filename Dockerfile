FROM openjdk:11
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "${JAVA_OPTS}", "-jar", "/app.jar"]