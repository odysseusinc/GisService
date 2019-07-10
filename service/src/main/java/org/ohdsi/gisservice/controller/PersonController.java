package org.ohdsi.gisservice.controller;

import java.util.Date;
import java.util.Optional;
import org.ohdsi.gisservice.dto.PersonLocationHistory;
import org.ohdsi.gisservice.service.PersonService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping(value = "/api/v1/person")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {

        this.personService = personService;
    }

    @RequestMapping(
        value = "/{personId}/bounds/{dataSourceKey}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public PersonLocationHistory getClusters(
            @PathVariable("personId") Integer personId,
            @PathVariable("dataSourceKey") String dataSourceKey,
            @RequestParam(name = "startDate", required = false) Long startTimestamp,
            @RequestParam(name = "endDate", required = false) Long endTimestamp
    ) throws IOException {

        Date startDate = Optional.ofNullable(startTimestamp).map(Date::new).orElse(null),
                endDate = Optional.ofNullable(endTimestamp).map(Date::new).orElse(null);
        return personService.getBoundsHistory(personId, dataSourceKey, startDate, endDate);
    }
}
