package de.blackforestsolutions.dravelopsstationpersistenceapi.controller;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.TravelPointApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("travelpoints/get")
public class TravelPointController {

    private final TravelPointApiService travelPointApiService;

    @Autowired
    public TravelPointController(TravelPointApiService travelPointApiService) {
        this.travelPointApiService = travelPointApiService;
    }

    @RequestMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TravelPoint> getAllTravelPoints() {
        return travelPointApiService.getAllTravelPoints();
    }


}
