package de.blackforestsolutions.dravelopsstationpersistenceapi.controller;

import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.GeocodingService;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("geocoding/get")
public class GeocodingController {

    private final GeocodingService geocodingService;

    @Autowired
    public GeocodingController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @RequestMapping(value = "/operatingPolygon", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Polygon> getOperatingPolygon() {
        return geocodingService.getPolygonFromAllStops();
    }

    @RequestMapping(value = "/operatingBox", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Box> getOperatingBox() {
        return geocodingService.getBoxFromAllStops();
    }


}
