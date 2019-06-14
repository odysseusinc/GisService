package org.ohdsi.gisservice.service;

import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import org.ohdsi.gisservice.model.Source;
import org.ohdsi.gisservice.repository.SourceRepository;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class SourceService {

    private SourceRepository sourceRepository;
    private ConversionService conversionService;

    public SourceService(SourceRepository sourceRepository, ConversionService conversionService) {

        this.sourceRepository = sourceRepository;
        this.conversionService = conversionService;
    }

    public Source getByKey(String key) {

        return sourceRepository.getOne(key);
    }

    public DataSourceUnsecuredDTO getDataSourceDTO(String dataSourceKey) {

        Source source = getByKey(dataSourceKey);
        return conversionService.convert(source, DataSourceUnsecuredDTO.class);
    }
}
