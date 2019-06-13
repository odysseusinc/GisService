package org.ohdsi.gisservice.service;

import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import org.ohdsi.gisservice.model.Source;
import org.ohdsi.gisservice.repository.SourceRepository;
import org.springframework.stereotype.Service;

@Service
public class SourceService {

    private SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {

        this.sourceRepository = sourceRepository;
    }

    public Source getByKey(String key) {

        return sourceRepository.getOne(key);
    }

    public DataSourceUnsecuredDTO getDataSourceDTO(String dataSourceKey) {

        Source source = getByKey(dataSourceKey);

        DataSourceUnsecuredDTO dataSourceDTO = new DataSourceUnsecuredDTO();
        dataSourceDTO.setType(source.getDialect());
        dataSourceDTO.setConnectionString(source.getConnectionString());
        dataSourceDTO.setUsername(source.getUsername());
        dataSourceDTO.setPassword(source.getPassword());

        return dataSourceDTO;
    }
}
