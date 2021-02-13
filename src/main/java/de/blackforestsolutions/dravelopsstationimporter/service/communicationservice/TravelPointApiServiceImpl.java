package de.blackforestsolutions.dravelopsstationimporter.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationimporter.configuration.GtfsApiTokenConfiguration;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

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
        return gtfsApiTokenConfiguration.getApitokens()
                .stream()
                .map(this::convertConfigTokenToApiToken)
                .collect(Collectors.toList());
    }

    private ApiToken convertConfigTokenToApiToken(GtfsApiTokenConfiguration.ApiToken apiToken) {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(apiToken.getProtocol())
                .setHost(apiToken.getHost())
                .setPath(apiToken.getPath())
                .setHeaders(apiToken.getHeaders())
                .build();
    }

    /**
     * This method distinct travelPoints with the same name, platforms and coordinates
     * FE_FLOATING_POINT_EQUALITY warning indicates an overflow error for a double.
     * This warning is suppressed as we dont want a tolerance here
     *
     * @return Predicate to distinct equal TravelPoints
     */
    private static Predicate<TravelPoint> distinctEquivalentTravelPoints() {
        Set<TravelPoint> savedTravelPoints = ConcurrentHashMap.newKeySet();
        return t -> {
            for (TravelPoint savedTravelPoint : savedTravelPoints) {

                if (savedTravelPoint.getName().equals(t.getName()) && savedTravelPoint.getPoint().getX() == t.getPoint().getX() && savedTravelPoint.getPoint().getY() == t.getPoint().getY() && savedTravelPoint.getPlatform().equals(t.getPlatform())) {
                    return false;
                }
            }
            return savedTravelPoints.add(t);
        };
    }
}
