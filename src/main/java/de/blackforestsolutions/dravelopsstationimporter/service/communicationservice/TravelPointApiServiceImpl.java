package de.blackforestsolutions.dravelopsstationimporter.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationimporter.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationimporter.service.repositoryservice.TravelPointRepositoryService;
import de.blackforestsolutions.dravelopsstationimporter.service.supportservice.UuidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Slf4j
@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

    private final GtfsApiService gtfsApiService;
    private final TravelPointRepositoryService travelPointRepositoryService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final UuidService uuidService;
    private final List<ApiToken> gtfsApiTokens;

    @Autowired
    public TravelPointApiServiceImpl(GtfsApiService gtfsApiService, TravelPointRepositoryService travelPointRepositoryService, ExceptionHandlerService exceptionHandlerService, UuidService uuidService, List<ApiToken> gtfsApiTokens) {
        this.gtfsApiService = gtfsApiService;
        this.travelPointRepositoryService = travelPointRepositoryService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.uuidService = uuidService;
        this.gtfsApiTokens = gtfsApiTokens;
    }

    @Override
    @Scheduled(fixedRateString = "${gtfs.update.period.milliseconds}")
    public void updateTravelPoints() {
        try {
            log.info("Starting to download gtfs...");
            gtfsApiService.getAllTravelPointsBy(gtfsApiTokens)
                    .flatMap(exceptionHandlerService::handleExceptions)
                    .filter(distinctEquivalentTravelPoints())
                    .collectMap(travelPoint -> uuidService.createUUID(), travelPoint -> travelPoint)
                    .subscribe(travelPointRepositoryService::replaceAllTravelPoints);
        } catch (Exception e) {
            exceptionHandlerService.handleExceptions(e);
        }
    }

    @Override
    public Flux<TravelPoint> getAllTravelPoints() {
        try {
            return Flux.fromIterable(travelPointRepositoryService.getAllTravelPoints())
                    .onErrorResume(exceptionHandlerService::handleExceptions);
        } catch (Exception e) {
            return exceptionHandlerService.handleExceptions(e);
        }
    }

    private static Predicate<TravelPoint> distinctEquivalentTravelPoints() {
        Set<TravelPoint> savedTravelPoints = ConcurrentHashMap.newKeySet();
        return t -> {
            for (TravelPoint savedTravelPoint : savedTravelPoints) {
                if (savedTravelPoint.getName().equals(t.getName()) && savedTravelPoint.getPoint().getX() == t.getPoint().getX() && savedTravelPoint.getPoint().getY() == t.getPoint().getY()) {
                    return false;
                }
            }
            return savedTravelPoints.add(t);
        };
    }
}
