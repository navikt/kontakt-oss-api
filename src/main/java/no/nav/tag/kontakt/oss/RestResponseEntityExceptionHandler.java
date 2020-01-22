package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.salesforce.SalesforceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);


    @ExceptionHandler(value = {SalesforceException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleSalesForceException(RuntimeException e, WebRequest webRequest) {
        return getResponseEntity(e, "Intern tjenefeil", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {KontaktskjemaException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleKontaktskjemaException(RuntimeException e, WebRequest webRequest) {
        return getResponseEntity(e, "Intern tjenefeil", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleFeilIKontaktskjemaException(RuntimeException e, WebRequest webRequest) {
        return getResponseEntity(e, "Innsendt informasjon er ugyldig", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    protected ResponseEntity<Object> handleGenerellException(RuntimeException e, WebRequest webRequest) {
        logger.error("Uhåndtert feil", e);
        return getResponseEntity(e, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> getResponseEntity(RuntimeException e, String melding, HttpStatus status) {
        HashMap<String, String> body = new HashMap<>(1);
        body.put("message", melding);
        logger.info(
                String.format(
                        "Returnerer følgende HttpStatus '%s' med melding '%s' pga exception '%s'",
                        status.toString(),
                        melding,
                        e.getMessage()
                )
        );

        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON_UTF8).body(body);
    }

}
