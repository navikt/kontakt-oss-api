package no.nav.tag.kontakt.oss.geografi;

import no.nav.tag.kontakt.oss.geografi.integrasjon.NorgKlient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeografiController {

    private final NorgKlient norgKlient;

    @Autowired
    public GeografiController(NorgKlient norgKlient) {
        this.norgKlient = norgKlient;
    }

    @GetMapping(value = "${controller.basepath}/geografi")
    public FylkesinndelingMedNavenheter geografi() {
        // TODO TAG-298 Raffiner denne dataen
        return norgKlient.hentGeografiFraNorg();
    }

    @GetMapping(value = "${controller.basepath}/organisering")
    // TODO TAG-298 Raffiner denne dataen
    public List<NorgOrganisering> organisering() {
        return norgKlient.hentOrganiseringFraNorg();
    }
}
