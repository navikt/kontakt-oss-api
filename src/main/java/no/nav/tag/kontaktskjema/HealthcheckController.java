package no.nav.tag.kontaktskjema;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthcheckController {

    @RequestMapping(value = "/kontaktskjema/isAlive", method = RequestMethod.GET)
    @ResponseBody
    public String isAlive() {
        return "ok";
    }

    @RequestMapping(value = "/kontaktskjema/isReady", method = RequestMethod.GET)
    @ResponseBody
    public String isReady() {
        return "ok";
    }
}
