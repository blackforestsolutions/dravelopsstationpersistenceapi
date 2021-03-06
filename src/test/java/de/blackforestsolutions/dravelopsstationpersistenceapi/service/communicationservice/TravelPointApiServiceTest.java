package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import com.hazelcast.core.HazelcastException;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.GtfsApiTokenConfiguration;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryServiceImpl;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.supportservice.UuidService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.supportservice.UuidServiceImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getRnvGtfsApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.*;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.testutil.TestUtils.convertApiTokensToConfigToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TravelPointApiServiceTest {

    private final GtfsApiService gtfsApiService = mock(GtfsApiServiceImpl.class);
    private final TravelPointRepositoryService travelPointRepositoryService = mock(TravelPointRepositoryServiceImpl.class);
    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final UuidService uuidService = mock(UuidServiceImpl.class);
    private final GtfsApiTokenConfiguration gtfsApiTokenConfiguration = convertApiTokensToConfigToken(List.of(getRnvGtfsApiToken()));

    private final TravelPointApiService classUnderTest = new TravelPointApiServiceImpl(gtfsApiService, travelPointRepositoryService, exceptionHandlerService, uuidService, gtfsApiTokenConfiguration);

    @BeforeEach
    void init() {
        when(gtfsApiService.getAllTravelPointsBy(anyList())).thenReturn(Flux.just(
                new CallStatus<>(getFurtwangenBirkeTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getBadDuerkheimTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getNeckarauTrainStationTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getKarlsbaderStreetTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getTribergStationStreetTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getTribergStationStreetTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getWrongCoordinateTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(null, Status.FAILED, new Exception())
        ));

        when(uuidService.createUUID())
                .thenReturn(TEST_UUID_1)
                .thenReturn(TEST_UUID_2)
                .thenReturn(TEST_UUID_3)
                .thenReturn(TEST_UUID_4)
                .thenReturn(TEST_UUID_5);

        when(travelPointRepositoryService.replaceAllTravelPoints(anyMap())).thenReturn(List.of(
                getFurtwangenBirkeTravelPoint(),
                getBadDuerkheimTravelPoint(),
                getNeckarauTrainStationTravelPoint(),
                getKarlsbaderStreetTravelPoint(),
                getTribergStationStreetTravelPoint()
        ));

        when(travelPointRepositoryService.getAllTravelPoints()).thenReturn(List.of(
                getFurtwangenBirkeTravelPoint(),
                getBadDuerkheimTravelPoint(),
                getNeckarauTrainStationTravelPoint(),
                getKarlsbaderStreetTravelPoint(),
                getTribergStationStreetTravelPoint()
        ));
    }

    @Test
    void test_getAllTravelPoints_returns_all_travelPoints() {

        Flux<TravelPoint> result = classUnderTest.getAllTravelPoints();

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenBirkeTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getKarlsbaderStreetTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getTribergStationStreetTravelPoint()))
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPoints_is_executed_correctly() {

        classUnderTest.getAllTravelPoints().collectList().block();

        verify(travelPointRepositoryService, times(1)).getAllTravelPoints();
    }

    @Test
    void test_getAllTravelPoints_with_zero_travelPoints_in_hazelcast_returns_zero_travelPoints() {
        when(travelPointRepositoryService.getAllTravelPoints()).thenReturn(Collections.emptyList());

        Flux<TravelPoint> result = classUnderTest.getAllTravelPoints();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPoints_returns_zero_travelPoints_when_exception_is_thrown() {
        when(travelPointRepositoryService.getAllTravelPoints()).thenThrow(new HazelcastException());

        Flux<TravelPoint> result = classUnderTest.getAllTravelPoints();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_updateTravelPoints_handles_callStatus_and_uuid_creation_correctly() {
        ArgumentCaptor<List<ApiToken>> gtfsApiTokensArg = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Map<UUID, TravelPoint>> travelPointsArg = ArgumentCaptor.forClass(Map.class);

        classUnderTest.updateTravelPoints();

        Awaitility.await()
                .untilAsserted(() -> {
                    verify(gtfsApiService, times(1)).getAllTravelPointsBy(gtfsApiTokensArg.capture());
                    verify(exceptionHandlerService, times(8)).handleExceptions(any(CallStatus.class));
                    verify(uuidService, times(5)).createUUID();
                    verify(travelPointRepositoryService, times(1)).replaceAllTravelPoints(travelPointsArg.capture());
                    assertThat(gtfsApiTokensArg.getValue().get(0)).isEqualToComparingFieldByFieldRecursively(getRnvGtfsApiToken());
                    assertThat(travelPointsArg.getValue().size()).isEqualTo(5);
                    assertThat(travelPointsArg.getValue().get(TEST_UUID_1)).isEqualToComparingFieldByFieldRecursively(getFurtwangenBirkeTravelPoint());
                    assertThat(travelPointsArg.getValue().get(TEST_UUID_2)).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint());
                    assertThat(travelPointsArg.getValue().get(TEST_UUID_3)).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint());
                    assertThat(travelPointsArg.getValue().get(TEST_UUID_4)).isEqualToComparingFieldByFieldRecursively(getKarlsbaderStreetTravelPoint());
                    assertThat(travelPointsArg.getValue().get(TEST_UUID_5)).isEqualToComparingFieldByFieldRecursively(getTribergStationStreetTravelPoint());
                });
    }

    @Test
    void test_updateTravelPoints_handles_the_exception_when_exception_is_thrown() {
        ArgumentCaptor<Exception> exceptionArg = ArgumentCaptor.forClass(Exception.class);
        when(travelPointRepositoryService.replaceAllTravelPoints(anyMap()))
                .thenThrow(new HazelcastException());

        classUnderTest.updateTravelPoints();

        Awaitility.await()
                .untilAsserted(() -> {
                    verify(exceptionHandlerService, times(1)).handleExceptions(exceptionArg.capture());
                    assertThat(exceptionArg.getValue()).isInstanceOf(Exception.class);
                });
    }
}
