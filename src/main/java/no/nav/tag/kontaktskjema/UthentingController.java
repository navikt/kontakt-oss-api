package no.nav.tag.kontaktskjema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(prefix = "feature.toggle", name = "uthentingsendepunkt", havingValue="enabled")
@CrossOrigin(origins = {"https://tjenester.nav.no", "https://tjenester-q1.nav.no", "https://tjenester-t1.nav.no"})
@RestController
public class UthentingController {

    private final KontaktskjemaRepository repository;

    @Autowired
    public UthentingController(KontaktskjemaRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "${controller.basepath}/internal/hentAlle")
    public Iterable<Kontaktskjema> hentAlle() {
        return repository.findAll();
    }

}
