FROM maven:3.5.4-jdk-11-slim as builder
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn clean package

FROM navikt/java:11
ENV SPRING_PROFILES_ACTIVE postgres
COPY --from=builder /target/tag-kontaktskjema-0.0.1-SNAPSHOT.jar app.jar