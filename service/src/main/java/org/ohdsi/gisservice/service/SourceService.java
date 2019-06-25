package org.ohdsi.gisservice.service;

import com.odysseusinc.arachne.commons.types.DBMSType;
import com.odysseusinc.arachne.commons.utils.QuoteUtils;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.KerberosAuthMechanism;
import org.jasypt.encryption.StringEncryptor;
import org.ohdsi.gisservice.model.Source;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SourceService {

    private JdbcTemplate jdbcTemplate;
    private ConversionService conversionService;
    private final EncryptionService encryptionService;

    // {h-schema} placeholder doesn't seem to work in entityManager.createQuery
    // same as in @Subselect annotation on top of @Entity (https://hibernate.atlassian.net/browse/HHH-7913)
    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    public SourceService(JdbcTemplate jdbcTemplate, ConversionService conversionService,
                         EncryptionService encryptionService) {

        this.jdbcTemplate = jdbcTemplate;
        this.conversionService = conversionService;
        this.encryptionService = encryptionService;
    }

    public Source getByKey(String key) {

        String sql = "SELECT\n" +
        "  s.source_key as key,\n" +
        "  s.source_name as name,\n" +
        "  UPPER(s.source_dialect) as dialect,\n" +
        "  s.source_connection as connection_string,\n" +
        "  s.username,\n" +
        "  s.password,\n" +
        "  s.krb_auth_method,\n" +
        "  s.keytab_name,\n" +
        "  s.krb_keytab,\n" +
        "  s.krb_admin_server,\n" +
        "  cdm_daimon.table_qualifier as cdm_schema,\n" +
        "  results_daimon.table_qualifier as results_schema,\n" +
        "  temp_daimon.table_qualifier as temp_schema\n" +
        "FROM " + schema + ".source s\n" +
        "  LEFT JOIN " + schema + ".source_daimon cdm_daimon on s.source_id = cdm_daimon.source_id AND cdm_daimon.daimon_type = 0\n" +
        "  LEFT JOIN " + schema + ".source_daimon results_daimon on s.source_id = results_daimon.source_id AND results_daimon.daimon_type = 2\n" +
        "  LEFT JOIN " + schema + ".source_daimon temp_daimon on s.source_id = temp_daimon.source_id AND temp_daimon.daimon_type = 5\n" +
        "WHERE s.deleted_date IS NULL AND s.source_key = '" + QuoteUtils.escapeSql(key) + "'";

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

    public DataSourceUnsecuredDTO getDataSourceDTO(String dataSourceKey) {

        Source source = getByKey(dataSourceKey);
        return conversionService.convert(source, DataSourceUnsecuredDTO.class);
    }
}
