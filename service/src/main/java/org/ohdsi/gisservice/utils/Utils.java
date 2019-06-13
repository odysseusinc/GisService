package org.ohdsi.gisservice.utils;

import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Utils {

    // TODO: reuse SqlUtils from EE / WebAPI + BigQuery & Kerberos auth
    public static JdbcTemplate getJdbcTemplate(DataSourceUnsecuredDTO dataSourceData) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
              dataSourceData.getConnectionString(),
              dataSourceData.getUsername(),
              dataSourceData.getPassword()
      );

      return new JdbcTemplate(dataSource);
    }
}
