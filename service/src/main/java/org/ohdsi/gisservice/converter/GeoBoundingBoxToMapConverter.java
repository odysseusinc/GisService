package org.ohdsi.gisservice.converter;

import org.ohdsi.gisservice.dto.GeoBoundingBox;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GeoBoundingBoxToMapConverter extends BaseConversionServiceAwareConverter<GeoBoundingBox, Map> {

    @Override
    public Map convert(GeoBoundingBox geoBoundingBox) {

        Map<String, Object> params = new HashMap<>();
        params.put("westLongitude", geoBoundingBox.getWestLongitude());
        params.put("eastLongitude", geoBoundingBox.getEastLongitude());
        params.put("northLatitude", geoBoundingBox.getNorthLatitude());
        params.put("southLatitude", geoBoundingBox.getSouthLatitude());
        return params;
    }
}
