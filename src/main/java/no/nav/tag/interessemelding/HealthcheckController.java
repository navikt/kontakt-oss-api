package no.nav.tag.interessemelding;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthcheckController {

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    @ResponseBody
    public Interessemelding isAlive() {
        return new Interessemelding();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    @ResponseBody
    public Interessemelding isReady() {
        return new Interessemelding();
    }
}
