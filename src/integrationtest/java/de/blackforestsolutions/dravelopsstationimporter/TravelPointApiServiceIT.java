package de.blackforestsolutions.dravelopsstationimporter;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationimporter.service.communicationservice.TravelPointApiService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.*;
import static de.blackforestsolutions.dravelopsstationimporter.configuration.HazelcastConfiguration.HAZELCAST_INSTANCE;
import static de.blackforestsolutions.dravelopsstationimporter.configuration.HazelcastConfiguration.TRAVEL_POINT_MAP;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelPointApiServiceIT {

    @Qualifier(HAZELCAST_INSTANCE)
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private TravelPointApiService classUnderTest;

    @Test
    void test_updateTravelPoints_inserts_new_travelPoints() {
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastInstance.getMap(TRAVEL_POINT_MAP);

        classUnderTest.updateTravelPoints();

        Awaitility.await()
                .untilAsserted(() -> assertThat(hazelcastTravelPoints.size()).isGreaterThan(0));
    }

    @Test
    void test_getAllTravelPoints_returns_all_travelPoints_in_hazelcast() {
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastInstance.getMap(TRAVEL_POINT_MAP);
        hazelcastTravelPoints.put(TEST_UUID_1, getFurtwangenBirkeTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_2, getBadDuerkheimTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_3, getNeckarauTrainStationTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_4, getKarlsbaderStreetTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_5, getTribergStationStreetTravelPoint());

        Flux<TravelPoint> result = classUnderTest.getAllTravelPoints();

        StepVerifier.create(result)
                .expectNextCount(5L)
                .verifyComplete();
    }
}
