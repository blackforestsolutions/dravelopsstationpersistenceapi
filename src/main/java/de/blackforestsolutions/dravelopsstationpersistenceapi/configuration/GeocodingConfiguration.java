package de.blackforestsolutions.dravelopsstationpersistenceapi.configuration;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class GeocodingConfiguration {

    public static final int DEGREES_COORDINATE_SYSTEM = 4326;

    @Value("${graphql.playground.tabs[6].bufferInMetres}")
    private int bufferInMetres;

    @Bean
    public GeometryFactory geometryFactory() {
        PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.FLOATING);
        return new GeometryFactory(precisionModel, DEGREES_COORDINATE_SYSTEM);
    }

    @Bean
    public int bufferInMetres() {
        return bufferInMetres;
    }
}
