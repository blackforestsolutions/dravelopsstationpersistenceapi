package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.Box;
import org.locationtech.jts.geom.Polygon;
import reactor.core.publisher.Mono;

public interface GeocodingService {
    Mono<Polygon> getPolygonFromAllStops();

    Mono<Box> getBoxFromAllStops();
}
