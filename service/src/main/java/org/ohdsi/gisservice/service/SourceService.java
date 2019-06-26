package org.ohdsi.gisservice.service;

import com.odysseusinc.arachne.commons.types.DBMSType;
import com.odysseusinc.arachne.commons.utils.QuoteUtils;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.KerberosAuthMechanism;
import com.odysseusinc.datasourcemanager.krblogin.KerberosService;
import com.odysseusinc.datasourcemanager.krblogin.RuntimeServiceMode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ohdsi.gisservice.converter.DataSourceMapper;
import org.ohdsi.gisservice.model.Source;
import org.ohdsi.gisservice.utils.JdbcTemplateConsumer;
import org.ohdsi.sql.SqlRender;
import org.ohdsi.sql.SqlTranslate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SourceService {

    private final JdbcTemplate jdbcTemplate;
    private final EncryptionService encryptionService;
    private final DataSourceMapper dataSourceMapper;
    private final KerberosService kerberosService;

    // {h-schema} placeholder doesn't seem to work in entityManager.createQuery
    // same as in @Subselect annotation on top of @Entity (https://hibernate.atlassian.net/browse/HHH-7913)
    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    public Source getByKey(String key) throws IOException {

        String sql;

        try(InputStream in = new ClassPathResource("/sql/getSource.sql").getInputStream()) {
            sql = IOUtils.toString(in, StandardCharsets.UTF_8);
            sql = SqlRender.renderSql(sql, new String[]{ "webapiSchema", "sourceKey" }, new String[]{ schema, QuoteUtils.escapeSql(key) });
            sql = SqlTranslate.translateSql(sql, "postgresql"); //TODO add webapi dialect
        }

        Source source = new Source();

        jdbcTemplate.query(sql, rs -> {
            source.setKey(rs.getString("key"));
            source.setSourceName(rs.getString("name"));
            source.setDialect(DBMSType.valueOf(rs.getString("dialect")));
            source.setConnectionString(rs.getString("connection_string"));
            source.setUsername(encryptionService.decrypt(rs.getString("username")));
            source.setPassword(encryptionService.decrypt(rs.getString("password")));
            source.setKrbAuthMethod(KerberosAuthMechanism.valueOf(rs.getString("krb_auth_method")));
            source.setKeyfileName(rs.getString("keytab_name"));
            source.setKeyfile(rs.getBytes("krb_keytab"));
            source.setKrbAdminServer(rs.getString("krb_admin_server"));
            source.setCdmSchema(rs.getString("cdm_schema"));
            source.setResultsSchema(rs.getString("results_schema"));
            source.setTempSchema(rs.getString("temp_schema"));
        });

        return source;
    }

    public DataSourceUnsecuredDTO getDataSourceDTO(String dataSourceKey) throws IOException {

        Source source = getByKey(dataSourceKey);
        return dataSourceMapper.toDatasourceUnsecuredDTO(source);
    }

    public <T> T executeOnSource(DataSourceUnsecuredDTO dataSourceData, JdbcTemplateConsumer<T> consumer) throws IOException {

        if (Objects.isNull(consumer)) {
            throw new IllegalArgumentException("consumer is required");
        }

        File tempDir = Files.createTempDirectory("gis").toFile();

        // Kerberos
        if (dataSourceData.getUseKerberos()) {
            kerberosService.runKinit(dataSourceData, RuntimeServiceMode.SINGLE, tempDir);
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                dataSourceData.getConnectionString(),
                dataSourceData.getUsername(),
                dataSourceData.getPassword()
        );
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        try {
            return consumer.execute(jdbcTemplate);
        } finally {
            FileUtils.deleteQuietly(tempDir);
        }
    }
}
