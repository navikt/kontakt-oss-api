package no.nav.tag.kontaktskjema;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthcheckController {

    @RequestMapping(value = "/tag-kontaktskjema/isAlive", method = RequestMethod.GET)
    @ResponseBody
    public String isAlive() {
        return "ok";
    }

    @RequestMapping(value = "/tag-kontaktskjema/isReady", method = RequestMethod.GET)
    @ResponseBody
    public String isReady() {
        return "ok";
    }
}
