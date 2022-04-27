package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.integrasjon.ClassificationCode;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.integrasjon.SSBKlient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FylkesinndelingService {
    private static final String MUNICIPALITY_CLASSIFICATION = "131";
    private static final String CITY_DISTRICT_CLASSIFICATION = "103";
    private final ApplicationEventPublisher eventPublisher;
    private final FylkesinndelingDAO kommuneDAO;
    private final SSBKlient ssbKlient;


    public FylkesinndelingService(FylkesinndelingDAO kommuneDAO, SSBKlient ssbKlient, ApplicationEventPublisher eventPublisher) {
        this.kommuneDAO = kommuneDAO;
        this.ssbKlient = ssbKlient;
        this.eventPublisher = eventPublisher;
    }


    public List<KommuneEllerBydel> hentFylkesinndelinger(){
        try{
            return kommuneDAO.hentFylkesinndelinger();
        } catch (Exception e){
            throw new IllegalStateException("Kunne ikke hente fylkesinndelinger"+ e.getMessage());
        }
    }

    public void oppdatereFylkesinndeling(){
        try{
            List<ClassificationCode> municipalityCodes = ssbKlient.getClassificationCodes(MUNICIPALITY_CLASSIFICATION);
            List<ClassificationCode> cityDistrictCodes = ssbKlient.getClassificationCodes(CITY_DISTRICT_CLASSIFICATION);
            List<KommuneEllerBydel> munis = mergeListOfMunicipalitiesAndDistricts(municipalityCodes, cityDistrictCodes);
            kommuneDAO.oppdatereFylkesinndelinger(munis);

            eventPublisher.publishEvent(new FylkesinndelingOppdatert(true));
            log.info("Informasjon om fylkesinndeling ble oppdatert");
        } catch(Exception e){
            eventPublisher.publishEvent(new FylkesinndelingOppdatert(false));
            throw new KontaktskjemaException("Kunne ikke oppdatere fylkesinndeling", e);
        }
    }


    public List<KommuneEllerBydel> mergeListOfMunicipalitiesAndDistricts(List<ClassificationCode> municipalityCodes, List<ClassificationCode> cityDistrictCodes){

        Map<String, KommuneEllerBydel> municipalityDivision = municipalityCodes.stream()
                .filter(c-> c.getName().equals("Uoppgitt") == false)
                .map(c-> new KommuneEllerBydel(c.getCode(), c.getName()))
                .collect(Collectors.toMap(KommuneEllerBydel::getNummer, Function.identity()));


        for(ClassificationCode c :cityDistrictCodes){
            if(c.getName().equals("Uoppgitt")) {
                continue;
            }

            if(!municipalityDivision.containsKey(c.getCode())){
                municipalityDivision.put(c.getCode(), new KommuneEllerBydel());
            }
            municipalityDivision.get(c.getCode()).setNummer(c.getCode());
            municipalityDivision.get(c.getCode()).setNavn(municipalityDivision.get(c.getCode().substring(0, 4)).getNavn() + "- " + c.getName());
        }

        return new ArrayList<>(municipalityDivision.values());

    }

}
