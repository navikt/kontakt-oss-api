package no.nav.tag.kontakt.oss.geografi;

import no.nav.tag.kontakt.oss.geografi.integrasjon.KommuneEllerBydel;
import no.nav.tag.kontakt.oss.geografi.integrasjon.NorgKlient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @GetMapping(value = "${controller.basepath}/enhetsMap")
    // TODO TAG-298 Raffiner denne dataen
    public Map<KommuneEllerBydel, String> enhetsMap() {
        List<KommuneEllerBydel> kommuneEllerBydels = geografi().getGeografiMap().values().iterator().next();
        return norgKlient.hentMapFraKommuneEllerBydelTilNavenhet(kommuneEllerBydels);
    }

    @GetMapping(value = "${controller.basepath}/enhetsMap/{geografi}")
    // TODO TAG-298 Raffiner denne dataen
    public String enhetsMap(
            @PathVariable("geografi") String kommuneNrEllerBydelsNr
    ) {
        Optional<String> str = norgKlient.hentTilhoerendeNavenhet(kommuneNrEllerBydelsNr);
        return str.orElse("Fant ikke tilhørende enhet");
    }
}
