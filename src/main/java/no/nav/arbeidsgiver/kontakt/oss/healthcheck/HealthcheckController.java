package no.nav.arbeidsgiver.kontakt.oss.healthcheck;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal")
public class HealthcheckController {

    @GetMapping("/isReady")
    public ResponseEntity<?> isReady() { return ResponseEntity.status(200).build();}

    @GetMapping("/isAlive")
    public ResponseEntity<?> isAlive() {return ResponseEntity.status(200).build();}
}


