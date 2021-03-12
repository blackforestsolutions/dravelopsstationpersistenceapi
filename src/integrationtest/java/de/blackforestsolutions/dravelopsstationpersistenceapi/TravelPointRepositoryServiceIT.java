package de.blackforestsolutions.dravelopsstationpersistenceapi;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.*;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.HAZELCAST_INSTANCE;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.TRAVEL_POINT_MAP;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TravelPointRepositoryServiceIT {

    @Qualifier(HAZELCAST_INSTANCE)
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private TravelPointRepositoryService classUnderTest;

    @BeforeEach
    void init() {
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastInstance.getMap(TRAVEL_POINT_MAP);
        hazelcastTravelPoints.put(TEST_UUID_1, getFurtwangenBirkeTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_2, getTribergStationStreetTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_3, getBadDuerkheimTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_4, getNeckarauTrainStationTravelPoint());
    }

    @Test
    void test_getAllTravelPoints_returns_all_inserted_travelPoints() {

        List<TravelPoint> result = classUnderTest.getAllTravelPoints();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint());
        assertThat(result.get(1)).isEqualToComparingFieldByFieldRecursively(getFurtwangenBirkeTravelPoint());
        assertThat(result.get(2)).isEqualToComparingFieldByFieldRecursively(getTribergStationStreetTravelPoint());
        assertThat(result.get(3)).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint());
    }

    @Test
    void test_replaceAllTravelPoints_returns_all_replaced_travelPoints() {
        Map<UUID, TravelPoint> testData = Map.of(TEST_UUID_1, getFurtwangenBirkeTravelPoint());

        List<TravelPoint> result = classUnderTest.replaceAllTravelPoints(testData);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(getFurtwangenBirkeTravelPoint());
    }

    @Test
    void test_getAllTravelPointCoordinates_returns_all_inserted_coordinates() {

        List<Point> result = classUnderTest.getAllTravelPointCoordinates();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint().getPoint());
        assertThat(result.get(1)).isEqualToComparingFieldByFieldRecursively(getFurtwangenBirkeTravelPoint().getPoint());
        assertThat(result.get(2)).isEqualToComparingFieldByFieldRecursively(getTribergStationStreetTravelPoint().getPoint());
        assertThat(result.get(3)).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint().getPoint());
    }

    @AfterEach
    void tearDown() {
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastInstance.getMap(TRAVEL_POINT_MAP);
        hazelcastTravelPoints.clear();
    }
}
