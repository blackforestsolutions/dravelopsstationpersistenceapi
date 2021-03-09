package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.*;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.restcalls.CallService;
import lombok.extern.slf4j.Slf4j;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class GtfsApiServiceImpl implements GtfsApiService {

    private final CallService callService;

    @Autowired
    public GtfsApiServiceImpl(CallService callService) {
        this.callService = callService;
    }

    @Override
    public Flux<CallStatus<TravelPoint>> getAllTravelPointsBy(List<ApiToken> apiTokens) {
        return Flux.fromIterable(apiTokens)
                .flatMap(this::executeApiCall);
    }

    /**
     * This call is non reactive as it handles files. We only use here a flux to have an unique syntax
     *
     * @param apiToken with information where to download the gtfs file
     * @return travelPoints extracted from stops.txt
     */
    private Flux<CallStatus<TravelPoint>> executeApiCall(ApiToken apiToken) {
        try {
            Objects.requireNonNull(apiToken.getGtfsProvider(), "gtfsProvider is not allowed to be null");
            Objects.requireNonNull(apiToken.getGtfsUrl(), "gtfsUrl is not allowed to be null");
            Objects.requireNonNull(apiToken.getHeaders(), "headers is not allowed to be null");
            log.info("Downloading file from ".concat(apiToken.getGtfsProvider()).concat("-TravelProvider with url: ").concat(apiToken.getGtfsUrl()));
            File gtfsZip = callService.getFile(apiToken.getGtfsUrl(), convertApiTokenHeadersMap(apiToken));
            if (gtfsZip != null) {
                log.info("File successfully downloaded.");
            }
            return Flux.fromIterable(extractStopsFrom(gtfsZip))
                    .map(this::handleTravelPointMapping)
                    .onErrorResume(e -> Mono.just(new CallStatus<>(null, Status.FAILED, e)));

        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }
    }

    private Collection<Stop> extractStopsFrom(File gtfsZip) throws IOException {
        GtfsReader gtfsReader = new GtfsReader();
        // set the input file
        gtfsReader.setInputLocation(gtfsZip);
        // starts to read the file
        GtfsDaoImpl store = new GtfsDaoImpl();
        gtfsReader.setEntityStore(store);
        gtfsReader.run();

        return store.getAllStops();
    }


    private CallStatus<TravelPoint> handleTravelPointMapping(Stop stop) {
        try {
            TravelPoint travelPoint = extractTravelPointFrom(stop);
            return new CallStatus<>(travelPoint, Status.SUCCESS, null);
        } catch (Exception e) {
            return new CallStatus<>(null, Status.FAILED, e);
        }
    }

    private HttpHeaders convertApiTokenHeadersMap(ApiToken apiToken) {
        HttpHeaders headers = new HttpHeaders();
        apiToken.getHeaders().forEach(headers::add);
        return headers;
    }

    private TravelPoint extractTravelPointFrom(Stop stop) {
        return new TravelPoint.TravelPointBuilder()
                .setName(stop.getName())
                .setPoint(new Point.PointBuilder(stop.getLon(), stop.getLat()).build())
                .setPlatform(Optional.ofNullable(stop.getPlatformCode()).orElse(""))
                .build();
    }
}
