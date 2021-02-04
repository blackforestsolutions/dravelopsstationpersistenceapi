package de.blackforestsolutions.dravelopsstationimporter.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;

public interface TravelPointApiService {
    @Scheduled(fixedRateString = "${gtfs.update.period.milliseconds}")
    void updateTravelPoints();

    Flux<TravelPoint> getAllTravelPoints();
}
