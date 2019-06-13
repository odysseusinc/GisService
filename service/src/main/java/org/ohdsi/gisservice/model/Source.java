package org.ohdsi.gisservice.model;

import com.odysseusinc.arachne.commons.types.DBMSType;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.KerberosAuthMechanism;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Immutable
// TODO: extract SQL to external file
@Subselect("SELECT\n" +
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
        "FROM source s\n" +
        "  LEFT JOIN source_daimon cdm_daimon on s.source_id = cdm_daimon.source_id AND cdm_daimon.daimon_type = 0\n" +
        "  LEFT JOIN source_daimon results_daimon on s.source_id = results_daimon.source_id AND results_daimon.daimon_type = 2\n" +
        "  LEFT JOIN source_daimon temp_daimon on s.source_id = temp_daimon.source_id AND temp_daimon.daimon_type = 5\n" +
        "WHERE s.deleted_date IS NULL")
@Getter
public class Source {

    @Id
    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String sourceName;

    @Column(name = "dialect")
    @Enumerated(EnumType.STRING)
    private DBMSType dialect;

    @Column(name = "connection_string")
    private String connectionString;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "krb_auth_method")
    @Enumerated(EnumType.STRING)
    private KerberosAuthMechanism krbAuthMethod;

    @Column(name = "keytab_name")
    private String keyfileName;

    @Column(name = "krb_keytab")
    private byte[] keyfile;

    @Column(name = "krb_admin_server")
    private String krbAdminServer;

    @Column(name = "cdm_schema")
    private String cdmSchema;

    @Column(name = "results_schema")
    private String resultsSchema;

    @Column(name = "temp_schema")
    private String tempSchema;

}
