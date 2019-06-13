package org.ohdsi.gisservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.ohdsi.gisservice.dto.GeoBoundingBox;
import org.ohdsi.gisservice.service.CohortService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@CrossOrigin // TODO: remove
@RequestMapping(value = "/api/v1/cohort")
public class CohortController {

    private final CohortService cohortService;

    public CohortController(CohortService cohortService) {

        this.cohortService = cohortService;
    }

    @RequestMapping(
        value = "/{cohortId}/bounds/{dataSourceKey}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public GeoBoundingBox getClusters(
        @PathVariable("cohortId") Integer cohortId,
        @PathVariable("dataSourceKey") String dataSourceKey
    ) throws IOException, SQLException {

        return cohortService.getCohortBounds(cohortId, dataSourceKey);
    }

    @RequestMapping(
        value = "/{cohortId}/clusters/{dataSourceKey}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public JsonNode getClusters(
        @PathVariable("cohortId") Integer cohortId,
        @PathVariable("dataSourceKey") String dataSourceKey,
        GeoBoundingBox geoBoundingBox
    ) throws IOException {

        return cohortService.getClusters(cohortId, dataSourceKey, geoBoundingBox);
    }

    @RequestMapping(
        value = "/{cohortId}/density/{dataSourceKey}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public JsonNode getDensityMap(
        @PathVariable("cohortId") Integer cohortId,
        @PathVariable("dataSourceKey") String dataSourceKey,
        GeoBoundingBox geoBoundingBox
    ) throws IOException {

        return cohortService.getDensityMap(cohortId, dataSourceKey, geoBoundingBox);
    }
}
