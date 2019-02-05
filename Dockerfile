FROM navikt/java:11
COPY --from=builder /target/kontakt-oss-api-0.0.1-SNAPSHOT.jar app.jar