package no.nav.tag.interessemelding;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthcheckController {

    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    @ResponseBody
    public String isAlive() {
        return "ok";
    }

    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    @ResponseBody
    public String isReady() {
        return "ok";
    }
}
