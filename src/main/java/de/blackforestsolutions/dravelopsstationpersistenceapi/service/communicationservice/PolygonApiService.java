package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import org.locationtech.jts.geom.Polygon;
import reactor.core.publisher.Mono;

public interface PolygonApiService {
    Mono<Polygon> getPolygonFromAllStops();
}
