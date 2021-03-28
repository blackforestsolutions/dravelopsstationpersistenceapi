package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import com.hazelcast.core.HazelcastException;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryServiceImpl;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxWithNoEmptyFields;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.GeocodingConfiguration.DEGREES_COORDINATE_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GeocodingServiceTest {

    private final TravelPointRepositoryService travelPointRepositoryService = mock(TravelPointRepositoryServiceImpl.class);
    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), DEGREES_COORDINATE_SYSTEM);

    private final GeocodingService classUnderTest = new GeocodingServiceImpl(travelPointRepositoryService, exceptionHandlerService, geometryFactory);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(classUnderTest, "bufferInMetres", 0);

        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenReturn(List.of(
                new Point.PointBuilder(0.0d, 0.0d).build(),
                new Point.PointBuilder(0.0d, 10.0d).build(),
                new Point.PointBuilder(10.0d, 10.0d).build(),
                new Point.PointBuilder(10.0d, 0.0d).build()
        ));
    }

    @Test
    void test_getPolygonFromAllStops_returns_correct_polygon_with_no_buffer() {

        Mono<Polygon> result = classUnderTest.getPolygonFromAllStops();

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
    void test_getPolygonFromAllStops_returns_correct_polygon_with_buffer() {
        ReflectionTestUtils.setField(classUnderTest, "bufferInMetres", 110574);

        Mono<Polygon> result = classUnderTest.getPolygonFromAllStops();

        StepVerifier.create(result)
                .assertNext(polygon -> {
                    assertThat(polygon.getExteriorRing().getCoordinates()).extracting(
                            Coordinate::getX,
                            Coordinate::getY
                    ).containsExactly(
                            Tuple.tuple(-0.004561040111617007d, -0.9927228027547199d),
                            Tuple.tuple(10.003006304704753d, -1.0079815384575677d),
                            Tuple.tuple(10.195521161247413, -0.9887845431993816),
                            Tuple.tuple(10.380746300716234d, -0.9324462632772917d),
                            Tuple.tuple(10.551713081619118d, -0.8410262046110369d),
                            Tuple.tuple(10.701980051585126d, -0.7179193808714132d),
                            Tuple.tuple(10.825879583514137d, -0.5677351784785197d),
                            Tuple.tuple(10.918736427202049d, -0.3961264990770741d),
                            Tuple.tuple(10.977048887538135d, -0.20957482119706317d),
                            Tuple.tuple(10.998624606719192d, -0.015139433217966125d),
                            Tuple.tuple(10.974948808704989d, 10.017253282214465d),
                            Tuple.tuple(10.952348973897964d, 10.209823587881292d),
                            Tuple.tuple(10.892914867924983d, 10.394004375643283d),
                            Tuple.tuple(10.799037391715066d, 10.562666852854813d),
                            Tuple.tuple(10.674424876866413d, 10.70932921951735d),
                            Tuple.tuple(10.523946381451989d, 10.828401494569725d),
                            Tuple.tuple(10.353434469228787d, 10.915388657455129d),
                            Tuple.tuple(10.16945677621713d, 10.96704656524484d),
                            Tuple.tuple(9.97906578230911d, 10.98148784595978d),
                            Tuple.tuple(0.019504159753616208d, 10.965917683217809d),
                            Tuple.tuple(-0.17260281503443514d, 10.950810835677025d),
                            Tuple.tuple(-0.3579896837000286d, 10.89844632609598d),
                            Tuple.tuple(-0.529428097519545d, 10.810836074869599d),
                            Tuple.tuple(-0.6802125543431138d, 10.691354053803883d),
                            Tuple.tuple(-0.8044164396135451d, 10.54461711013526d),
                            Tuple.tuple(-0.8971234837933111d, 10.376318774535818d),
                            Tuple.tuple(-0.9546256972397593d, 10.193019686403927d),
                            Tuple.tuple(-0.9745790186127664d, 10.001900314255447d),
                            Tuple.tuple(-0.9990162543063537d, 4.5345524837400405E-4d),
                            Tuple.tuple(-0.9801000893667516d, -0.19264734147793272d),
                            Tuple.tuple(-0.9237080392539165d, -0.3784268594421085d),
                            Tuple.tuple(-0.83198290614357d, -0.5497704402688295d),
                            Tuple.tuple(-0.7084244329303078d, -0.7001140548978875d),
                            Tuple.tuple(-0.5577551412829409d, -0.8236979089108103d),
                            Tuple.tuple(-0.3857385916245818d, -0.9157881347571085d),
                            Tuple.tuple(-0.1989575595661542d, -0.97285809985723d),
                            Tuple.tuple(-0.004561040111617007d, -0.9927228027547199d)
                    );
                });
    }

    @Test
    void test_getPolygonFromAllStops_is_executed_correctly() {

        classUnderTest.getPolygonFromAllStops().block();

        verify(travelPointRepositoryService, times(1)).getAllTravelPointCoordinates();
    }

    @Test
    void test_getPolygonFromAllStops_returns_no_polygon_when_exception_is_thrown() {
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenThrow(new HazelcastException());

        Mono<Polygon> result = classUnderTest.getPolygonFromAllStops();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_returns_correct_box_with_no_buffer() {

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .assertNext(box -> assertThat(box).isEqualToComparingFieldByFieldRecursively(getBoxWithNoEmptyFields()))
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_returns_correct_box_with_buffer() {
        ReflectionTestUtils.setField(classUnderTest, "bufferInMetres", 110574);

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .assertNext(box -> {
                    assertThat(box.getTopLeft()).isEqualToComparingFieldByField(new Point.PointBuilder(-0.9990162543063537d, 10.98148784595978d).build());
                    assertThat(box.getBottomRight()).isEqualToComparingFieldByField(new Point.PointBuilder(10.998624606719192d, -1.0079815384575677d).build());
                })
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_is_executed_correctly() {

        classUnderTest.getBoxFromAllStops().block();

        verify(travelPointRepositoryService, times(1)).getAllTravelPointCoordinates();
    }

    @Test
    void test_getBoxFromAllStops_returns_no_box_when_exception_is_thrown() {
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenThrow(new HazelcastException());

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
