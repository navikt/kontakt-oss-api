package no.nav.tag.kontaktskjema;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthcheckController {

    @RequestMapping(value = "${controller_basepath}/isAlive", method = RequestMethod.GET)
    @ResponseBody
    public String isAlive() {
        return "ok";
    }

    @RequestMapping(value = "${controller_basepath}/isReady", method = RequestMethod.GET)
    @ResponseBody
    public String isReady() {
        return "ok";
    }
}
