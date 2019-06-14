package org.ohdsi.gisservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odysseusinc.arachne.commons.utils.TemplateUtils;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.AnalysisSyncRequestDTO;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import lombok.var;
import org.apache.commons.io.IOUtils;
import org.ohdsi.gisservice.dto.GeoBoundingBox;
import org.ohdsi.gisservice.service.client.ExecutionEngineClient;
import org.ohdsi.gisservice.utils.Utils;
import org.ohdsi.sql.SqlRender;
import org.ohdsi.sql.SqlTranslate;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CohortService {
    private static final String GET_COHORT_BOUNDS_SQL_PATH = "/bounds/getCohortBounds.sql";

    private static final String CALCULATE_CONTOURS_R_PATH = "/density/calculateContours.R";
    private static final String CALCULATE_CONTOURS_RESULT_FILE = "geo.json";

    private static final String CALCULATE_CLUSTERS_R_PATH = "/cluster/calculateClusters.R";
    private static final String CALCULATE_CLUSTERS_RESULT_FILE = "clusters.json";

    private static final String COHORT_ID_PARAM = "cohortId";

    private final ExecutionEngineClient executionEngineClient;
    private final SourceService sourceService;
    private final ObjectMapper objectMapper;
    private final GenericConversionService conversionService;

    public CohortService(ExecutionEngineClient executionEngineClient, SourceService sourceService, ObjectMapper objectMapper, GenericConversionService conversionService) {

        this.executionEngineClient = executionEngineClient;
        this.sourceService = sourceService;
        this.objectMapper = objectMapper;
        this.conversionService = conversionService;
    }

    @PreAuthorize("hasPermission(#cohortId, 'cohortdefinition', 'get') && hasPermission(#dataSourceKey, 'source', 'access')")
    public GeoBoundingBox getCohortBounds(Integer cohortId, String dataSourceKey) throws IOException, SQLException {

        DataSourceUnsecuredDTO source = sourceService.getDataSourceDTO(dataSourceKey);

        String sql;
        try (InputStream is = ClassLoader.class.getResourceAsStream(GET_COHORT_BOUNDS_SQL_PATH)) {
            sql = IOUtils.toString(is);
            sql = SqlRender.renderSql(sql, new String[]{"cdmSchema", "resultSchema", "cohortId"}, new String[]{source.getCdmSchema(), source.getResultSchema(), cohortId.toString()});
            sql = SqlTranslate.translateSql(sql, source.getType().getOhdsiDB());
        }

        var bbox = new GeoBoundingBox();
        JdbcTemplate jdbcTemplate = Utils.getJdbcTemplate(source);
        jdbcTemplate.query(sql, rs -> {
            bbox.setNorthLatitude(rs.getDouble("max_latitude"));
            bbox.setWestLongitude(rs.getDouble("max_longitude"));
            bbox.setSouthLatitude(rs.getDouble("min_latitude"));
            bbox.setEastLongitude(rs.getDouble("min_longitude"));

        });
        return bbox;
    }

    @PreAuthorize("hasPermission(#cohortId, 'cohortdefinition', 'get') && hasPermission(#dataSourceKey, 'source', 'access')")
    public JsonNode getDensityMap(Integer cohortId, String dataSourceKey, GeoBoundingBox geoBoundingBox) throws IOException {

        return runGisAnalysis(cohortId, dataSourceKey, geoBoundingBox, CALCULATE_CONTOURS_R_PATH, CALCULATE_CONTOURS_RESULT_FILE);
    }

    @PreAuthorize("hasPermission(#cohortId, 'cohortdefinition', 'get') && hasPermission(#dataSourceKey, 'source', 'access')")
    public JsonNode getClusters(Integer cohortId, String dataSourceKey, GeoBoundingBox geoBoundingBox) throws IOException {

        return runGisAnalysis(cohortId, dataSourceKey, geoBoundingBox, CALCULATE_CLUSTERS_R_PATH, CALCULATE_CLUSTERS_RESULT_FILE);
    }

    private JsonNode runGisAnalysis(Integer cohortId, String dataSourceKey, GeoBoundingBox geoBoundingBox, String scriptPath, String resultFilename) throws IOException {

        String scriptFn = new File(scriptPath).getName();

        DataSourceUnsecuredDTO dataSourceDTO = sourceService.getDataSourceDTO(dataSourceKey);

        Map<String, Object> params = conversionService.convert(geoBoundingBox, Map.class);
        params.put(COHORT_ID_PARAM, cohortId);
        String script = TemplateUtils.loadTemplate(scriptPath).apply(params);

        var analysisRequest = buildRequest(dataSourceDTO, scriptFn);

        MultipartFile[] multipartFiles = executionEngineClient.executeSync(new HashMap<String, Object>() {
            {
                put("analysisRequest", analysisRequest);
                put("file", new MockMultipartFile(scriptFn, scriptFn, null, script.getBytes()));
            }
        });

        return resolveJsonResult(multipartFiles, resultFilename);
    }

    private AnalysisSyncRequestDTO buildRequest(DataSourceUnsecuredDTO dataSourceDTO, String executableFilename) {

        AnalysisSyncRequestDTO analysisRequest = new AnalysisSyncRequestDTO();
        analysisRequest.setId(0L);
        analysisRequest.setDataSource(dataSourceDTO);
        analysisRequest.setRequested(new Date());
        analysisRequest.setExecutableFileName(executableFilename);
        return analysisRequest;
    }

    private JsonNode resolveJsonResult(MultipartFile[] resultFiles, String filename) {

        MultipartFile resFile = Arrays.stream(resultFiles)
                .filter(mf -> Objects.equals(mf.getOriginalFilename(), filename))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot extract result file"));

        try {
            return objectMapper.readTree(new String(resFile.getBytes(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new UncheckedIOException("Cannot parse result file", ex);
        }
    }
}
