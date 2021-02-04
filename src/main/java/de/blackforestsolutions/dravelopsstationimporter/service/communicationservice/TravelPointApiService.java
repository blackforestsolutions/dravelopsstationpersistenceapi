package de.blackforestsolutions.dravelopsstationimporter.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import reactor.core.publisher.Flux;

public interface TravelPointApiService {
    void updateTravelPoints();

    Flux<TravelPoint> getAllTravelPoints();
}
