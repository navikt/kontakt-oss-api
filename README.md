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

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

* Lars Andreas Tveiten, lars.andreas.van.woensel.kooy.tveiten@nav.no
* Thomas Dufourd, thomas.dufourd@nav.no
* Malaz Alkoj, malaz.alkoj@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #arbeidsgiver-teamia.
