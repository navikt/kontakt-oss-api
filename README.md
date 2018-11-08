#TAG - Kontaktskjema for inkludering
===========================
[![CircleCI](https://circleci.com/gh/navikt/tiltaksgjennomforing.svg?style=svg)](https://circleci.com/gh/navikt/tiltaksgjennomforing)

Bygg image
`docker build -t interessemelding .`

Kjør container
`docker run -d -p 8080:80 interessemelding`

Åpnes i browser: [http://localhost:8080/](http://localhost:8080/)

Hvis localhost ikke fungerer, prøv med IP-en du finner med følgende kommando:

`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container_name_or_id`

Nais-url: https://interessemelding.nais.oera-q.local