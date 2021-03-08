package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.GtfsApiTokenConfiguration;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.supportservice.UuidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.blackforestsolutions.dravelopsstationpersistenceapi.utils.TravelPointUtils.distinctEquivalentTravelPoints;

@Slf4j
@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

    private static final double WRONG_COORDINATE = 0.0d;

    private final GtfsApiService gtfsApiService;
    private final TravelPointRepositoryService travelPointRepositoryService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final UuidService uuidService;
    private final GtfsApiTokenConfiguration gtfsApiTokenConfiguration;

    @Autowired
    public TravelPointApiServiceImpl(GtfsApiService gtfsApiService, TravelPointRepositoryService travelPointRepositoryService, ExceptionHandlerService exceptionHandlerService, UuidService uuidService, GtfsApiTokenConfiguration gtfsApiTokenConfiguration) {
        this.gtfsApiService = gtfsApiService;
        this.travelPointRepositoryService = travelPointRepositoryService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.uuidService = uuidService;
        this.gtfsApiTokenConfiguration = gtfsApiTokenConfiguration;
    }

    @Override
    @Scheduled(fixedRateString = "${gtfs.update.period.milliseconds}")
    public void updateTravelPoints() {
        try {
            log.info("Starting to download gtfs...");
            List<ApiToken> gtfsApiTokens = getApiTokens();
            gtfsApiService.getAllTravelPointsBy(gtfsApiTokens)
                    .flatMap(exceptionHandlerService::handleExceptions)
                    .filter(this::logAndFilerTravelPointsWithWrongCoordinates)
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

    private List<ApiToken> getApiTokens() {
        return gtfsApiTokenConfiguration.getApitokens().entrySet()
                .stream()
                .map(this::convertConfigTokenToApiToken)
                .collect(Collectors.toList());
    }

    private ApiToken convertConfigTokenToApiToken(Map.Entry<String, GtfsApiTokenConfiguration.ApiToken> apiToken) {
        return new ApiToken.ApiTokenBuilder()
                .setGtfsProvider(apiToken.getKey())
                .setGtfsUrl(apiToken.getValue().getGtfsUrl())
                .setHeaders(Optional.ofNullable(apiToken.getValue().getHeaders()).orElse(new HashMap<>()))
                .build();
    }

    private boolean logAndFilerTravelPointsWithWrongCoordinates(TravelPoint travelPoint) {
        if (travelPoint.getPoint().getX() == WRONG_COORDINATE && travelPoint.getPoint().getY() == WRONG_COORDINATE) {
            log.warn("No coordinates available for filtered TravelPoint: ".concat(travelPoint.getName()));
            return false;
        }
        return true;
    }
}
