package de.blackforestsolutions.dravelopsstationpersistenceapi.test;

import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.utils.TravelPointUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TravelPointUtilsTest {

    @Test
    void test_distinctEquivalentTravelPoints_returns_two_of_three_travelPoints() {
        Flux<TravelPoint> testData = Flux.just(
                getBadDuerkheimTravelPoint(),
                getBadDuerkheimTravelPoint(),
                getTribergStationStreetTravelPoint()
        );

        Flux<TravelPoint> result = testData.filter(TravelPointUtils.distinctEquivalentTravelPoints());

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getBadDuerkheimTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getTribergStationStreetTravelPoint()))
                .verifyComplete();
    }

    @Test
    void test_distinctEquivalentTravelPoints_with_two_equal_travelPoints_returns_one_travelPoint() {
        TravelPoint not_different = getTravelPointBuilderWithNoEmptyFields().build();
        Flux<TravelPoint> testData = Flux.just(not_different, not_different);

        Flux<TravelPoint> result = testData.filter(TravelPointUtils.distinctEquivalentTravelPoints());

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(not_different))
                .verifyComplete();
    }

    @Test
    void test_distinctEquivalentTravelPoints_with_different_name_returns_two_different_travelPoints() {
        TravelPoint first = getTravelPointBuilderWithNoEmptyFields().build();
        TravelPoint different = getTravelPointBuilderWithNoEmptyFields().setName("Different").build();
        Flux<TravelPoint> testData = Flux.just(first, different);

        Flux<TravelPoint> result = testData.filter(TravelPointUtils.distinctEquivalentTravelPoints());

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(first))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(different))
                .verifyComplete();
    }

    @Test
    void test_distinctEquivalentTravelPoints_with_different_platform_returns_two_different_travelPoints() {
        TravelPoint first = getTravelPointBuilderWithNoEmptyFields().build();
        TravelPoint different = getTravelPointBuilderWithNoEmptyFields().setPlatform("Different").build();
        Flux<TravelPoint> testData = Flux.just(first, different);

        Flux<TravelPoint> result = testData.filter(TravelPointUtils.distinctEquivalentTravelPoints());

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(first))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(different))
                .verifyComplete();
    }

    @Test
    void test_distinctEquivalentTravelPoints_with_different_longitude_returns_two_different_travelPoints() {
        TravelPoint first = getTravelPointBuilderWithNoEmptyFields().build();
        TravelPoint different = getTravelPointBuilderWithNoEmptyFields()
                .setPoint(new Point.PointBuilder(160.0d, first.getPoint().getY()).build())
                .build();
        Flux<TravelPoint> testData = Flux.just(first, different);

        Flux<TravelPoint> result = testData.filter(TravelPointUtils.distinctEquivalentTravelPoints());

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(first))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(different))
                .verifyComplete();
    }

    @Test
    void test_distinctEquivalentTravelPoints_with_different_latitude_returns_two_different_travelPoints() {
        TravelPoint first = getTravelPointBuilderWithNoEmptyFields().build();
        TravelPoint different = getTravelPointBuilderWithNoEmptyFields()
                .setPoint(new Point.PointBuilder(first.getPoint().getX(), 160.0d).build())
                .build();
        Flux<TravelPoint> testData = Flux.just(first, different);

        Flux<TravelPoint> result = testData.filter(TravelPointUtils.distinctEquivalentTravelPoints());

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(first))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(different))
                .verifyComplete();
    }
}
