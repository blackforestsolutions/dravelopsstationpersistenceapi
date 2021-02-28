package de.blackforestsolutions.dravelopsstationpersistenceapi.controller;

import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.GeocodingService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.GeocodingServiceImpl;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxWithNoEmptyFields;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.PolygonObjectMother.getPolygonWithNoEmptyFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GeocodingControllerTest {

    private final GeocodingService geocodingService = mock(GeocodingServiceImpl.class);

    private final GeocodingController classUnderTest = new GeocodingController(geocodingService);

    @Test
    void test_getOperatingPolygon_is_executed_correctly_and_returns_polygon() {
        when(geocodingService.getPolygonFromAllStops()).thenReturn(Mono.just(getPolygonWithNoEmptyFields()));

        Mono<Polygon> result = classUnderTest.getOperatingPolygon();

        verify(geocodingService, times(1)).getPolygonFromAllStops();
        StepVerifier.create(result)
                .assertNext(polygon -> assertThat(polygon).isEqualToComparingFieldByFieldRecursively(getPolygonWithNoEmptyFields()))
                .verifyComplete();
    }

    @Test
    void test_getOperatingPolygon_is_executed_correctly_when_no_polygon_is_available() {
        when(geocodingService.getPolygonFromAllStops()).thenReturn(Mono.empty());

        Mono<Polygon> result = classUnderTest.getOperatingPolygon();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getOperatingBox_is_executed_correctly_and_returns_box() {
        when(geocodingService.getBoxFromAllStops()).thenReturn(Mono.just(getBoxWithNoEmptyFields()));

        Mono<Box> result = classUnderTest.getOperatingBox();

        verify(geocodingService, times(1)).getBoxFromAllStops();
        StepVerifier.create(result)
                .assertNext(box -> assertThat(box).isEqualToComparingFieldByFieldRecursively(getBoxWithNoEmptyFields()))
                .verifyComplete();
    }

    @Test
    void test_getOperatingBox_is_executed_correctly_when_no_box_is_available() {
        when(geocodingService.getBoxFromAllStops()).thenReturn(Mono.empty());

        Mono<Box> result = classUnderTest.getOperatingBox();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
