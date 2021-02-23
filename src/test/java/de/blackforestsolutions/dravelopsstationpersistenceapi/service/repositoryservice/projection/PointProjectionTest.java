package de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.projection;

import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getBadDuerkheimTravelPoint;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.TEST_UUID_1;
import static org.assertj.core.api.Assertions.assertThat;

class PointProjectionTest {

    private final PointProjection classUnderTest = new PointProjection();

    @Test
    void test_transform_travelPointEntry_returns_correct_coordinate() {
        Map.Entry<UUID, TravelPoint> testData = Map.entry(TEST_UUID_1, getBadDuerkheimTravelPoint());

        Point result = classUnderTest.transform(testData);

        assertThat(result).isEqualToComparingFieldByField(getBadDuerkheimTravelPoint().getPoint());
    }
}
