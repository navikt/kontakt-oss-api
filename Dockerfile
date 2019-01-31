FROM maven:3.5.4-jdk-11-slim as builder
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn clean package

FROM navikt/java:11
COPY --from=builder /target/kontakt-oss-api-0.0.1-SNAPSHOT.jar app.jar