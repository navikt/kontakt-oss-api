#TAG - Kontaktskjema for inkludering
===========================
[![CircleCI](https://circleci.com/gh/navikt/kontakt-oss-api.svg?style=svg)](https://circleci.com/gh/navikt/kontakt-oss-api)

### Oppsett

Bygg image
`docker build -t kontakt-oss-api .`

Kjør container
`docker run -d -p 8080:80 kontakt-oss-api `

Åpnes i browser: [http://localhost:8080/](http://localhost:8080/)

Hvis localhost ikke fungerer, prøv med IP-en du finner med følgende kommando:

`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container_name_or_id`

