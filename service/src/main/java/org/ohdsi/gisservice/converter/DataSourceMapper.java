package org.ohdsi.gisservice.converter;

import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.DataSourceUnsecuredDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ohdsi.gisservice.model.Source;

@Mapper(componentModel = "spring")
public interface DataSourceMapper {

	@Mapping(source = "sourceName", target = "name")
	@Mapping(source = "dialect", target = "type")
	@Mapping(source = "resultsSchema", target = "resultSchema")
	DataSourceUnsecuredDTO toDatasourceUnsecuredDTO(Source source);
}
