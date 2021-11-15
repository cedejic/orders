FROM openjdk:11-jdk-slim
RUN adduser --system --group appuser
USER appuser:appuser
ARG JAR_FILE=target/orders.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]