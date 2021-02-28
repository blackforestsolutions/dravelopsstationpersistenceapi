package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.restcalls.CallServiceImpl;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getRnvGtfsApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getSbgGtfsApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.getResourceAsFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GtfsApiServiceTest {

    private final CallService callService = mock(CallServiceImpl.class);

    private final GtfsApiService classUnderTest = new GtfsApiServiceImpl(callService);

    @BeforeEach
    void init() {
        when(callService.getFile(anyString(), any(HttpHeaders.class)))
                .thenReturn(getResourceAsFile("gtfs/rnv.gtfs.zip", ".zip"))
                .thenReturn(getResourceAsFile("gtfs/sbg.gtfs.zip", ".zip"));
    }

    @Test
    void test_getAllTravelPointsBy_rnvApiToken_and_sbgApiToken_maps_all_travelPoints_correctly() {
        List<ApiToken> testData = List.of(
                getRnvGtfsApiToken(),
                getSbgGtfsApiToken()
        );

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject().getName()).isNotEmpty();
                    assertThat(travelPoint.getCalledObject().getPoint()).isNotNull();
                    assertThat(travelPoint.getCalledObject().getArrivalTime()).isNull();
                    assertThat(travelPoint.getCalledObject().getDepartureTime()).isNull();
                    assertThat(travelPoint.getCalledObject().getPlatform()).isNotNull();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPointsBy_rnvApiToken_and_sbgApiToken_returns_correct_number_of_travelPoints() {
        List<ApiToken> testData = List.of(
                getRnvGtfsApiToken(),
                getSbgGtfsApiToken()
        );

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData);

        StepVerifier.create(result)
                .expectNextCount(6222L)
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPointsBy_rnvApiToken_and_sbgApiToken_returns_correct_mapped_travelPoints() {
        List<ApiToken> testData = List.of(
                getRnvGtfsApiToken(),
                getSbgGtfsApiToken()
        );

        List<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData).collectList().block();

        assertThat(result).extracting(
                travelPoint -> travelPoint.getCalledObject().getName(),
                travelPoint -> travelPoint.getCalledObject().getPoint().getX(),
                travelPoint -> travelPoint.getCalledObject().getPoint().getY(),
                travelPoint -> travelPoint.getCalledObject().getPlatform()
        ).containsOnlyOnce(
                Tuple.tuple(
                        getFurtwangenBirkeTravelPoint().getName(),
                        getFurtwangenBirkeTravelPoint().getPoint().getX(),
                        getFurtwangenBirkeTravelPoint().getPoint().getY(),
                        getFurtwangenBirkeTravelPoint().getPlatform()
                ),
                Tuple.tuple(
                        getBadDuerkheimTravelPoint().getName(),
                        getBadDuerkheimTravelPoint().getPoint().getX(),
                        getBadDuerkheimTravelPoint().getPoint().getY(),
                        getBadDuerkheimTravelPoint().getPlatform()
                ),
                Tuple.tuple(
                        getNeckarauTrainStationTravelPoint().getName(),
                        getNeckarauTrainStationTravelPoint().getPoint().getX(),
                        getNeckarauTrainStationTravelPoint().getPoint().getY(),
                        getNeckarauTrainStationTravelPoint().getPlatform()
                ),
                Tuple.tuple(
                        getKarlsbaderStreetTravelPoint().getName(),
                        getKarlsbaderStreetTravelPoint().getPoint().getX(),
                        getKarlsbaderStreetTravelPoint().getPoint().getY(),
                        getKarlsbaderStreetTravelPoint().getPlatform()
                ),
                Tuple.tuple(
                        getTribergStationStreetTravelPoint().getName(),
                        getTribergStationStreetTravelPoint().getPoint().getX(),
                        getTribergStationStreetTravelPoint().getPoint().getY(),
                        getTribergStationStreetTravelPoint().getPlatform()
                )
        );
    }

    @Test
    void test_getAllTravelPointsBy_rnvApiToken_and_sbgApiToken_is_executed_correctly() {
        List<ApiToken> testData = List.of(
                getRnvGtfsApiToken(),
                getSbgGtfsApiToken()
        );
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeaderArg = ArgumentCaptor.forClass(HttpHeaders.class);

        classUnderTest.getAllTravelPointsBy(testData).collectList().block();

        verify(callService, times(2)).getFile(urlArg.capture(), httpHeaderArg.capture());
        assertThat(urlArg.getAllValues().size()).isEqualTo(2);
        assertThat(urlArg.getAllValues().get(0)).isEqualTo("https://gtfs-sandbox-dds.rnv-online.de/latest/gtfs.zip");
        assertThat(urlArg.getAllValues().get(1)).isEqualTo("http://nvbw.de/fileadmin/user_upload/service/open_data/fahrplandaten_mit_liniennetz/sbg.zip");
        assertThat(httpHeaderArg.getAllValues().size()).isEqualTo(2);
        assertThat(httpHeaderArg.getAllValues().get(0).entrySet().size()).isEqualTo(0);
        assertThat(httpHeaderArg.getAllValues().get(1).entrySet().size()).isEqualTo(1);
        assertThat(httpHeaderArg.getAllValues().get(1).get("Token").size()).isEqualTo(1);
        assertThat(httpHeaderArg.getAllValues().get(1).get("Token").get(0)).isEqualTo("123");
    }

    @Test
    void test_getAllTravelPointsBy_empty_apiToken_list_returns_zero_travelPoints() {
        List<ApiToken> testData = Collections.emptyList();

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPointsBy_rnvApiToken_and_sbgApiToken_returns_one_failed_call_Status_and_other_journeys_when_a_call_failes() {
        List<ApiToken> testData = List.of(
                getRnvGtfsApiToken(),
                getSbgGtfsApiToken()
        );
        when(callService.getFile(anyString(), any(HttpHeaders.class)))
                .thenThrow(new RestClientException(""))
                .thenReturn(getResourceAsFile("gtfs/sbg.gtfs.zip", ".zip"));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getCalledObject()).isNull();
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPoint.getThrowable()).isInstanceOf(Exception.class);
                })
                .expectNextCount(1L)
                .thenConsumeWhile(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject().getName()).isNotEmpty();
                    assertThat(travelPoint.getCalledObject().getPoint()).isNotNull();
                    assertThat(travelPoint.getCalledObject().getArrivalTime()).isNull();
                    assertThat(travelPoint.getCalledObject().getDepartureTime()).isNull();
                    assertThat(travelPoint.getCalledObject().getPlatform()).isNotNull();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPointsBy_rnvApiToken_sbgApiToken_returns_two_errors_when_callService_returns_null() {
        List<ApiToken> testData = List.of(
                getRnvGtfsApiToken(),
                getSbgGtfsApiToken()
        );
        when(callService.getFile(anyString(), any(HttpHeaders.class)))
                .thenReturn(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getCalledObject()).isNull();
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPoint.getThrowable()).isInstanceOf(Exception.class);
                })
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getCalledObject()).isNull();
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPoint.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPointsBy_error_token_and_gtfsUrl_as_null_returns_failed_callStatus_with_nullPointerException() {
        ApiToken.ApiTokenBuilder errorToken = new ApiToken.ApiTokenBuilder(getSbgGtfsApiToken());
        errorToken.setGtfsUrl(null);
        List<ApiToken> testData = List.of(errorToken.build());

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getCalledObject()).isNull();
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPoint.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPointsBy_error_token_and_headers_as_null_returns_failed_callStatus_with_nullPointerException() {
        ApiToken.ApiTokenBuilder errorToken = new ApiToken.ApiTokenBuilder(getSbgGtfsApiToken());
        errorToken.setHeaders(null);
        List<ApiToken> testData = List.of(errorToken.build());

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAllTravelPointsBy(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getCalledObject()).isNull();
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPoint.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

}
