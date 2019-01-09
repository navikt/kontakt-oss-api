package no.nav.tag.kontaktskjema.uthenting;

import no.nav.tag.kontaktskjema.KontaktskjemaRepository;
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
    private final UthentingUtils uthentingUtils;

    @Autowired
    public UthentingController(KontaktskjemaRepository repository, UthentingUtils uthentingUtils) {
        this.repository = repository;
        this.uthentingUtils = uthentingUtils;
    }

    @GetMapping(value = "${controller.basepath}/internal/hentAlle")
    public Iterable<KontaktskjemaUthenting> hentAlle() {
        return uthentingUtils.lagUthentinger(repository.findAll());
    }
}
