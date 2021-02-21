package de.blackforestsolutions.dravelopsstationpersistenceapi.configuration;

import org.springframework.beans.factory.annotation.Value;

public class PolygonConfiguration {

    @Value("{}")
    private String polygonBuffer;
}
