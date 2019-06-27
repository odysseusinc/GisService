package org.ohdsi.gisservice.converter;

import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import org.ohdsi.gisservice.model.Source;
import org.springframework.stereotype.Component;

@Component
public class SourceToDataSourceUnsecuredDTOConverter extends BaseConversionServiceAwareConverter<Source, DataSourceUnsecuredDTO> {

    @Override
    public DataSourceUnsecuredDTO convert(Source source) {

        DataSourceUnsecuredDTO result = new DataSourceUnsecuredDTO();
        result.setType(source.getDialect());
        result.setConnectionString(source.getConnectionString());
        result.setUsername(source.getUsername());
        result.setPassword(source.getPassword());
        result.setCdmSchema(source.getCdmSchema());
        result.setResultSchema(source.getResultsSchema());
        return result;
    }
}
