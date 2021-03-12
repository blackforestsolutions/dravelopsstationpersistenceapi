package de.blackforestsolutions.dravelopsstationpersistenceapi;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.GeocodingService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxWithNoEmptyFields;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getTravelPointWithNoEmptyFieldsBy;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.*;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.HAZELCAST_INSTANCE;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.TRAVEL_POINT_MAP;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class GeocodingServiceIT {

    @Qualifier(HAZELCAST_INSTANCE)
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private GeocodingService geocodingService;

    @BeforeEach
    void init() {
        IMap<UUID, TravelPoint> testData = hazelcastInstance.getMap(TRAVEL_POINT_MAP);
        testData.put(TEST_UUID_1, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(0.0d, 0.0d).build()));
        testData.put(TEST_UUID_2, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(0.0d, 10.0d).build()));
        testData.put(TEST_UUID_3, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(10.0d, 10.0d).build()));
        testData.put(TEST_UUID_4, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(10.0d, 0.0d).build()));
    }

    @Test
    void test_getPolygonFromAllStops_returns_correct_polygon() {

        Mono<Polygon> result = geocodingService.getPolygonFromAllStops();

        StepVerifier.create(result)
                .assertNext(polygon -> assertThat(polygon.getExteriorRing().getCoordinates()).extracting(
                            Coordinate::getX,
                            Coordinate::getY
                        ).containsExactly(
                            Tuple.tuple(0.0d, 0.0d),
                            Tuple.tuple(0.0d, 10.0d),
                            Tuple.tuple(10.0d, 10.0d),
                            Tuple.tuple(10.0d, 0.0d),
                            Tuple.tuple(0.0d, 0.0d)
                        )
                )
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_returns_correct_box() {

        Mono<Box> result = geocodingService.getBoxFromAllStops();

        StepVerifier.create(result)
                .assertNext(box -> assertThat(box).isEqualToComparingFieldByFieldRecursively(getBoxWithNoEmptyFields()))
                .verifyComplete();
    }

    @AfterEach
    void tearDown() {
        IMap<UUID, TravelPoint> testData = hazelcastInstance.getMap(TRAVEL_POINT_MAP);
        testData.clear();
    }
}
