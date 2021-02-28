package de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.*;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.TRAVEL_POINT_MAP;
import static org.assertj.core.api.Assertions.assertThat;

class TravelPointRepositoryServiceTest {

    private HazelcastInstance hazelcastMock;

    private TravelPointRepositoryService classUnderTest;

    @BeforeEach
    void init() {
        hazelcastMock = new TestHazelcastInstanceFactory(1).newHazelcastInstance();
        classUnderTest = new TravelPointRepositoryServiceImpl(hazelcastMock);
    }

    @AfterEach
    void tearDown() {
        Hazelcast.shutdownAll();
    }

    @Test
    void test_getAllTravelPoints_returns_all_inserted_travelPoints() {
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastMock.getMap(TRAVEL_POINT_MAP);
        hazelcastTravelPoints.put(TEST_UUID_1, getFurtwangenBirkeTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_2, getTribergStationStreetTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_3, getBadDuerkheimTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_4, getNeckarauTrainStationTravelPoint());

        List<TravelPoint> result = classUnderTest.getAllTravelPoints();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint());
        assertThat(result.get(1)).isEqualToComparingFieldByFieldRecursively(getFurtwangenBirkeTravelPoint());
        assertThat(result.get(2)).isEqualToComparingFieldByFieldRecursively(getTribergStationStreetTravelPoint());
        assertThat(result.get(3)).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint());
    }

    @Test
    void test_getAllTravelPoints_with_zero_travelPoints() {

        List<TravelPoint> result = classUnderTest.getAllTravelPoints();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void test_replaceAllTravelPoints_updates_hazelcast_correctly() {
        Map<UUID, TravelPoint> newHazelcastTravelPoints = Map.of(
                TEST_UUID_3, getBadDuerkheimTravelPoint(),
                TEST_UUID_4, getNeckarauTrainStationTravelPoint()
        );
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastMock.getMap(TRAVEL_POINT_MAP);
        hazelcastTravelPoints.put(TEST_UUID_1, getFurtwangenBirkeTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_2, getTribergStationStreetTravelPoint());

        classUnderTest.replaceAllTravelPoints(newHazelcastTravelPoints);

        assertThat(hazelcastTravelPoints.size()).isEqualTo(2);
        assertThat(hazelcastTravelPoints.get(TEST_UUID_3)).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint());
        assertThat(hazelcastTravelPoints.get(TEST_UUID_4)).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint());
    }

    @Test
    void test_replaceAllTravelPoints_returns_all_new_travelPoints() {
        Map<UUID, TravelPoint> newHazelcastTravelPoints = Map.of(
                TEST_UUID_3, getBadDuerkheimTravelPoint(),
                TEST_UUID_4, getNeckarauTrainStationTravelPoint()
        );
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastMock.getMap(TRAVEL_POINT_MAP);
        hazelcastTravelPoints.put(TEST_UUID_1, getFurtwangenBirkeTravelPoint());
        hazelcastTravelPoints.put(TEST_UUID_2, getTribergStationStreetTravelPoint());

        List<TravelPoint> result = classUnderTest.replaceAllTravelPoints(newHazelcastTravelPoints);

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void test_replaceAllTravelPoints_when_no_travelPoint_are_available_in_the_map() {
        Map<UUID, TravelPoint> newHazelcastTravelPoints = Map.of(
                TEST_UUID_3, getBadDuerkheimTravelPoint(),
                TEST_UUID_4, getNeckarauTrainStationTravelPoint()
        );
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastMock.getMap(TRAVEL_POINT_MAP);

        classUnderTest.replaceAllTravelPoints(newHazelcastTravelPoints);

        assertThat(hazelcastTravelPoints.size()).isEqualTo(2);
        assertThat(hazelcastTravelPoints.get(TEST_UUID_3)).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint());
        assertThat(hazelcastTravelPoints.get(TEST_UUID_4)).isEqualToComparingFieldByFieldRecursively(getNeckarauTrainStationTravelPoint());
    }

    @Test
    void test_getAllTravelPointCoordinates_returns_all_inserted_points() {
        IMap<UUID, TravelPoint> testData = hazelcastMock.getMap(TRAVEL_POINT_MAP);
        testData.put(TEST_UUID_1, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(0.0d, 0.0d).build()));
        testData.put(TEST_UUID_2, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(0.0d, 10.0d).build()));
        testData.put(TEST_UUID_3, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(10.0d, 10.0d).build()));
        testData.put(TEST_UUID_4, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(10.0d, 0.0d).build()));
        testData.put(TEST_UUID_5, getTravelPointWithNoEmptyFieldsBy(new Point.PointBuilder(0.0d, 0.0d).build()));

        List<Point> result = classUnderTest.getAllTravelPointCoordinates();

        assertThat(result.size()).isEqualTo(5);
        assertThat(result).extracting(
                Point::getX,
                Point::getY
        ).containsExactlyInAnyOrder(
                Tuple.tuple(0.0d, 0.0d),
                Tuple.tuple(0.0d, 10.0d),
                Tuple.tuple(10.0d, 10.0d),
                Tuple.tuple(10.0d, 0.0d),
                Tuple.tuple(0.0d, 0.0d)
        );
    }

    @Test
    void test_getAllTravelPointCoordinates_with_zero_travelPoints() {

        List<Point> result = classUnderTest.getAllTravelPointCoordinates();

        assertThat(result.size()).isEqualTo(0);
    }

}
