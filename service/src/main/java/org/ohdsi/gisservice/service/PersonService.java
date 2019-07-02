package org.ohdsi.gisservice.service;

import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import lombok.var;
import org.apache.commons.io.IOUtils;
import org.ohdsi.gisservice.dto.GeoBoundingBox;
import org.ohdsi.gisservice.dto.PersonLocation;
import org.ohdsi.gisservice.dto.PersonLocationHistory;
import org.ohdsi.sql.SqlRender;
import org.ohdsi.sql.SqlTranslate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PersonService {
    private static final String GET_PERSON_BOUNDS_SQL_PATH = "/bounds/getPersonLocations.sql";

    private final SourceService sourceService;

    public PersonService(SourceService sourceService) {

        this.sourceService = sourceService;
    }

    @PreAuthorize("hasPermission(#personId, #dataSourceKey + ':person', 'get')")
    public PersonLocationHistory getBoundsHistory(Integer personId, String dataSourceKey) throws IOException {

        DataSourceUnsecuredDTO source = sourceService.getDataSourceDTO(dataSourceKey);

        String sql;
        try (InputStream is = new ClassPathResource(GET_PERSON_BOUNDS_SQL_PATH).getInputStream()) {
            String sqlTmpl = IOUtils.toString(is, StandardCharsets.UTF_8);
            sqlTmpl = SqlRender.renderSql(sqlTmpl, new String[]{"cdmSchema", "personId"}, new String[]{source.getCdmSchema(), personId.toString()});
            sql = SqlTranslate.translateSql(sqlTmpl, source.getType().getOhdsiDB());
        }

        PersonLocationHistory locationHistory = sourceService.executeOnSource(source, jdbcTemplate -> {
            var lh = new PersonLocationHistory();
            jdbcTemplate.query(sql, rs -> {
                var location = new PersonLocation();
                location.setLatitude(rs.getDouble("latitude"));
                location.setLongitude(rs.getDouble("longitude"));
                location.setStartDate(rs.getDate("start_date"));
                location.setEndDate(rs.getDate("end_date"));
                lh.getLocations().add(location);
            });
            return lh;
        });

        var bbox = new GeoBoundingBox();
        bbox.setNorthLatitude(locationHistory.getLocations().stream().map(PersonLocation::getLatitude).max(Double::compare).get());
        bbox.setWestLongitude(locationHistory.getLocations().stream().map(PersonLocation::getLongitude).max(Double::compare).get());
        bbox.setSouthLatitude(locationHistory.getLocations().stream().map(PersonLocation::getLatitude).min(Double::compare).get());
        bbox.setEastLongitude(locationHistory.getLocations().stream().map(PersonLocation::getLongitude).min(Double::compare).get());
        locationHistory.setBbox(bbox);

        return locationHistory;
    }
}
