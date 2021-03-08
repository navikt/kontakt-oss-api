#Arbeidsgiver - Kontaktskjema for inkludering
===========================
Byggejobb: [GithubActions](https://github.com/navikt/kontakt-oss-api/actions)

Backend for kontaktskjema for arbeidsgivere
================

Appen mottar kontaktskjemaer innsendt av arbeidsgiver fra appen `kontakt-oss`.
 Det eksponeres også et endepunkt brukt av kontaktskjemaet, som gir ut alle kommuner som har et tilhørende NAV-kontor.

Hensikten er å fange opp flere henvendelser fra arbeidsgivere om rekruttering, inkludering og forebygging av sykefravær.

# Komme i gang

Koden kan kjøres som en vanlig Spring Boot-applikasjon fra KontaktskjemaApplication.

 Default spring-profil er local, og da er alle avhengigheter mocket på localhost:8081. 

## Docker

Bygg image
`docker build -t kontakt-oss-api .`

Kjør container
`docker run -d -p 8080:80 kontakt-oss-api `

Åpnes i browser: Nå kjører appen på localhost:8080

Hvis localhost ikke fungerer, prøv med IP-en du finner med følgende kommando:

`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container_name_or_id`

## Grafisk fremstilling av API-ene (swagger-ui)
API-et kan sees og testes på `http://localhost:8080/kontakt-oss-api/swagger-ui.html`

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan opprettes som github issues.
Eller for genereller spørsmål sjekk commit log for personer som aktivt jobber med koden.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #arbeidsgiver-min-side-arbeidsgiver.

# urler

dev: https://arbeidsgiver-q.nav.no/kontakt-oss (i vdi)
prod: https://arbeidsgiver.nav.no/kontakt-oss/kontaktskjema
logs: https://logs.adeo.no/app/dashboards#/view/754c72d0-76d8-11eb-90cb-7315dfb7dea6