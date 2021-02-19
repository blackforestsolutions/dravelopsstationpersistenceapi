package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GtfsApiService {
    Flux<CallStatus<TravelPoint>> getAllTravelPointsBy(List<ApiToken> apiTokens);
}
