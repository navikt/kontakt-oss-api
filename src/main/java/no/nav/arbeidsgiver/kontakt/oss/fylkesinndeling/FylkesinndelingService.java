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
            List<KommuneEllerBydel> inndelinger = mergeListOfMunicipalitiesAndDistricts(municipalityCodes, cityDistrictCodes);

            lagreOppdattertFylkesinndeling(inndelinger);

            eventPublisher.publishEvent(new FylkesinndelingOppdatert(true));
            log.info("Informasjon om fylkesinndeling ble oppdatert");
        } catch(Exception e){
            eventPublisher.publishEvent(new FylkesinndelingOppdatert(false));
            throw new KontaktskjemaException("Kunne ikke oppdatere fylkesinndeling\n "+ e.getMessage(), e);
        }
    }


    public List<KommuneEllerBydel> mergeListOfMunicipalitiesAndDistricts(List<ClassificationCode> municipalityCodes, List<ClassificationCode> cityDistrictCodes){
        List<KommuneEllerBydel> kommunerOgBydel = new ArrayList<>();

        for(ClassificationCode kommune: municipalityCodes){
            boolean hasBydel = false;
            for(ClassificationCode bydel: cityDistrictCodes){
                if(bydel.getCode().substring(0, 4).equals(kommune.getCode())){
                    hasBydel = true;
                    kommunerOgBydel.add(new KommuneEllerBydel(bydel.getCode(), kommune.getName() + "- "+bydel.getName()));
                }
            }
            if(!hasBydel){
                kommunerOgBydel.add(new KommuneEllerBydel(kommune.getCode(), kommune.getName()));
            }
        }

        return kommunerOgBydel;
    }

    private void lagreOppdattertFylkesinndeling(List<KommuneEllerBydel> inndelinger) throws Exception {
        int antallOppdatert = kommuneDAO.oppdatereFylkesinndelinger(inndelinger);
        if(antallOppdatert < 1){
            throw new IllegalStateException("Kunne ikke lagre oppdatert fylkesinndeling.");
        }
    }

}
