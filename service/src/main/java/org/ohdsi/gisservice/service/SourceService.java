package org.ohdsi.gisservice.service;

import com.odysseusinc.arachne.commons.types.DBMSType;
import com.odysseusinc.arachne.commons.utils.QuoteUtils;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.KerberosAuthMechanism;
import com.odysseusinc.datasourcemanager.krblogin.KerberosService;
import com.odysseusinc.datasourcemanager.jdbc.DataSourceJdbcExecutor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.persistence.EntityManager;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.ohdsi.gisservice.converter.DataSourceMapper;
import org.ohdsi.gisservice.model.Source;
import org.ohdsi.gisservice.utils.DbmsUtils;
import org.ohdsi.sql.SqlRender;
import org.ohdsi.sql.SqlTranslate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SourceService extends DataSourceJdbcExecutor {

    private final JdbcTemplate jdbcTemplate;
    private final EncryptionService encryptionService;
    private final DataSourceMapper dataSourceMapper;
    private final EntityManager entityManager;

    // {h-schema} placeholder doesn't seem to work in entityManager.createQuery
    // same as in @Subselect annotation on top of @Entity (https://hibernate.atlassian.net/browse/HHH-7913)
    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    public SourceService(JdbcTemplate jdbcTemplate, EncryptionService encryptionService, DataSourceMapper dataSourceMapper, KerberosService kerberosService, EntityManager entityManager) {

        super(kerberosService);
        this.jdbcTemplate = jdbcTemplate;
        this.encryptionService = encryptionService;
        this.dataSourceMapper = dataSourceMapper;

        this.entityManager = entityManager;
    }

    public Source getByKey(String key) throws IOException {

        String sql;

        try(InputStream in = new ClassPathResource("/sql/getSource.sql").getInputStream()) {
            Session session = entityManager.unwrap(Session.class);
            SessionFactory sessionFactory = session.getSessionFactory();
            Dialect dialect = ((SessionFactoryImplementor)sessionFactory).getJdbcServices().getDialect();
            sql = IOUtils.toString(in, StandardCharsets.UTF_8);
            sql = SqlRender.renderSql(sql, new String[]{ "webapiSchema", "sourceKey" }, new String[]{ schema, QuoteUtils.escapeSql(key) });
            sql = SqlTranslate.translateSql(sql, DbmsUtils.getCurrentDBMSType(dialect).getOhdsiDB());
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


}
