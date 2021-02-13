package de.blackforestsolutions.dravelopsstationimporter.controller;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationimporter.service.communicationservice.TravelPointApiService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TravelPointControllerTest {

    private final TravelPointApiService travelPointApiService = mock(TravelPointApiService.class);

    private final TravelPointController classUnderTest = new TravelPointController(travelPointApiService);

    @Test
    void test_getAllTravelPoints_is_executed_correctly_and_returns_travelPoints() {
        when(travelPointApiService.getAllTravelPoints()).thenReturn(Flux.just(
                getFurtwangenBirkeTravelPoint(),
                getTribergStationStreetTravelPoint(),
                getBadDuerkheimTravelPoint(),
                getNeckarauTrainStationTravelPoint()
        ));

        Flux<TravelPoint> result = classUnderTest.getAllTravelPoints();

        verify(travelPointApiService, times(1)).getAllTravelPoints();
        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenBirkeTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getTribergStationStreetTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint()))
                .verifyComplete();
    }

    @Test
    void test_getAllTravelPoints_is_executed_correctly_when_no_travelPoints_are_available() {
        when(travelPointApiService.getAllTravelPoints()).thenReturn(Flux.empty());

        Flux<TravelPoint> result = classUnderTest.getAllTravelPoints();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
