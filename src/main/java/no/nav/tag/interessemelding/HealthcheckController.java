package no.nav.tag.interessemelding;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthcheckController {

    @RequestMapping(value = "/internal/isAlive", method = RequestMethod.GET)
    @ResponseBody
    public String isAlive() {
        return "ok";
    }

    @RequestMapping(value = "/internal/isReady", method = RequestMethod.GET)
    @ResponseBody
    public String isReady() {
        return "ok";
    }
}
