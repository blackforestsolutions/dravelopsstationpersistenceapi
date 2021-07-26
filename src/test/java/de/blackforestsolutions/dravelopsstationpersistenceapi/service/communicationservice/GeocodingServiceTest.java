package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import com.hazelcast.core.HazelcastException;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.PolygonObjectMother.getHvvOperatingArea;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.PolygonObjectMother.getSbgOperatingArea;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.GeocodingConfiguration.DEGREES_COORDINATE_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

class GeocodingServiceTest {

    private final TravelPointRepositoryService travelPointRepositoryService = mock(TravelPointRepositoryServiceImpl.class);
    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), DEGREES_COORDINATE_SYSTEM);

    private final GeocodingService classUnderTest = new GeocodingServiceImpl(travelPointRepositoryService, exceptionHandlerService, geometryFactory);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(classUnderTest, "bufferInMetres", 0);
        List<Point> sbgTravelPoints = Arrays.stream(getSbgOperatingArea().getCoordinates())
                .map(coordinate -> new Point.PointBuilder(coordinate.x, coordinate.y).build())
                .collect(Collectors.toList());
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenReturn(sbgTravelPoints);
    }

    @Test
    void test_getPolygonFromAllStops_returns_correct_polygon_for_sbg_with_no_buffer() {

        Mono<Polygon> result = classUnderTest.getPolygonFromAllStops();

        StepVerifier.create(result)
                .assertNext(polygon -> assertArrayEquals(getSbgOperatingArea().getCoordinates(), polygon.getCoordinates()))
                .verifyComplete();
    }

    @Test
    void test_getPolygonFromAllStops_returns_correct_polygon_for_hvv_with_no_buffer() {
        List<Point> hvvTravelPoints = Arrays.stream(getHvvOperatingArea().getCoordinates())
                .map(coordinate -> new Point.PointBuilder(coordinate.x, coordinate.y).build())
                .collect(Collectors.toList());
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenReturn(hvvTravelPoints);

        Mono<Polygon> result = classUnderTest.getPolygonFromAllStops();

        StepVerifier.create(result)
                .assertNext(polygon -> assertArrayEquals(getHvvOperatingArea().getCoordinates(), polygon.getCoordinates()))
                .verifyComplete();
    }

    @Test
    void test_getPolygonFromAllStops_returns_correct_polygon_for_sbg_with_buffer() {
        ReflectionTestUtils.setField(classUnderTest, "bufferInMetres", 5000);

        Mono<Polygon> result = classUnderTest.getPolygonFromAllStops();

        StepVerifier.create(result)
                .assertNext(polygon -> assertArrayEquals(new Coordinate[]{
                        new Coordinate(7.703722118719157d, 47.49304086044343d),
                        new Coordinate(7.687654284993504d, 47.49365142153711d),
                        new Coordinate(7.672274095722977d, 47.49686288275248d),
                        new Coordinate(7.58043549134783d, 47.524627955498296d),
                        new Coordinate(7.568603044324274d, 47.52923774328677d),
                        new Coordinate(7.558409631761535d, 47.53538763954726d),
                        new Coordinate(7.550285669706272d, 47.54281833228403d),
                        new Coordinate(7.544574744352069d, 47.55121639258796d),
                        new Coordinate(7.286095727383401d, 48.059068437681695d),
                        new Coordinate(7.282766462935075d, 48.0692294695766d),
                        new Coordinate(7.2823852215689735d, 48.07176821224452d),
                        new Coordinate(7.282529227492065d, 48.081649675616035d),
                        new Coordinate(7.28589080808796d, 48.09127149766692d),
                        new Coordinate(7.292309333103289d, 48.100168941798024d),
                        new Coordinate(7.295756785579784d, 48.10384960498495d),
                        new Coordinate(7.3028748922270905d, 48.1101434449278d),
                        new Coordinate(7.311528446182899d, 48.115506051406356d),
                        new Coordinate(7.333318086204697d, 48.12679584542728d),
                        new Coordinate(7.333422871568092d, 48.12685000534641d),
                        new Coordinate(7.681532960494016d, 48.30526789647233d),
                        new Coordinate(7.691315284676028d, 48.30941296861108d),
                        new Coordinate(7.735204783793189d, 48.32463394620912d),
                        new Coordinate(7.744392627819402d, 48.32726037635772d),
                        new Coordinate(7.754062406753369d, 48.32895156146999d),
                        new Coordinate(8.481146751346214d, 48.41586484266177d),
                        new Coordinate(8.493195974554565d, 48.416533095914474d),
                        new Coordinate(8.505231024659745d, 48.41575991997037d),
                        new Coordinate(8.516865578921243d, 48.413570130229814d),
                        new Coordinate(8.875229959168276d, 48.32162786921763d),
                        new Coordinate(8.890180569038828d, 48.31622740553134d),
                        new Coordinate(8.90739854220849d, 48.30797657816089d),
                        new Coordinate(8.91674562363526d, 48.3025616237448d),
                        new Coordinate(8.924449316523065d, 48.29609730777297d),
                        new Coordinate(8.930250192462829d, 48.2888018754048d),
                        new Coordinate(8.933953325344504d, 48.28092153429855d),
                        new Coordinate(9.092461586392343d, 47.78439763232519d),
                        new Coordinate(9.093920777737463d, 47.775612949297994d),
                        new Coordinate(9.092803475323782d, 47.76680551539693d),
                        new Coordinate(9.081344994195398d, 47.72519400935012d),
                        new Coordinate(9.077815873848103d, 47.71689965122084d),
                        new Coordinate(9.072006370818206d, 47.7092123250754d),
                        new Coordinate(9.064130936839682d, 47.702414551450836d),
                        new Coordinate(9.05447962313311d, 47.696756057268416d),
                        new Coordinate(8.894238405307307d, 47.619439671921235d),
                        new Coordinate(8.884949079898236d, 47.61567644832413d),
                        new Coordinate(8.874881074970743d, 47.61298104353584d),
                        new Coordinate(8.453021254254873d, 47.52519411090938d),
                        new Coordinate(8.437031927564068d, 47.52324004301968d),
                        new Coordinate(7.703722118719157d, 47.49304086044343d)
                }, polygon.getCoordinates()))
                .verifyComplete();
    }

    @Test
    void test_getPolygonFromAllStops_is_executed_correctly() {

        classUnderTest.getPolygonFromAllStops().block();

        InOrder inOrder = inOrder(travelPointRepositoryService, exceptionHandlerService);
        inOrder.verify(travelPointRepositoryService, times(1)).getAllTravelPointCoordinates();
        inOrder.verify(exceptionHandlerService, times(0)).handleException(any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_getPolygonFromAllStops_returns_no_polygon_when_exception_is_thrown() {
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenThrow(new HazelcastException());

        Mono<Polygon> result = classUnderTest.getPolygonFromAllStops();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleException(any(Throwable.class));
    }

    @Test
    void test_getBoxFromAllStops_returns_correct_box_for_sbg_with_no_buffer() {

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .assertNext(box -> assertThat(box).isEqualToComparingFieldByFieldRecursively(getSbgBox()))
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_returns_correct_box_for_hvv_with_no_buffer() {
        List<Point> hvvTravelPoints = Arrays.stream(getHvvOperatingArea().getCoordinates())
                .map(coordinate -> new Point.PointBuilder(coordinate.x, coordinate.y).build())
                .collect(Collectors.toList());
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenReturn(hvvTravelPoints);

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .assertNext(box -> assertThat(box).isEqualToComparingFieldByFieldRecursively(getHvvBox()))
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_returns_correct_box_for_sbg_with_buffer() {
        ReflectionTestUtils.setField(classUnderTest, "bufferInMetres", 5000);

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .assertNext(box -> {
                    assertThat(box.getTopLeft()).isEqualTo(new Point.PointBuilder(7.2823852215689735d, 48.416533095914474d).build());
                    assertThat(box.getBottomRight()).isEqualTo(new Point.PointBuilder(9.093920777737463d, 47.49304086044343d).build());
                })
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_returns_correct_box_for_hvv_with_buffer() {
        ReflectionTestUtils.setField(classUnderTest, "bufferInMetres", 10000);
        List<Point> hvvTravelPoints = Arrays.stream(getHvvOperatingArea().getCoordinates())
                .map(coordinate -> new Point.PointBuilder(coordinate.x, coordinate.y).build())
                .collect(Collectors.toList());
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenReturn(hvvTravelPoints);

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .assertNext(box -> {
                    assertThat(box.getTopLeft()).isEqualTo(new Point.PointBuilder(8.1552482380657d, 54.99670479445762d).build());
                    assertThat(box.getBottomRight()).isEqualTo(new Point.PointBuilder(12.283577163272605d, 51.44697030952146d).build());
                })
                .verifyComplete();
    }

    @Test
    void test_getBoxFromAllStops_is_executed_correctly_with_no_buffer() {

        classUnderTest.getBoxFromAllStops().block();

        InOrder inOrder = inOrder(travelPointRepositoryService, exceptionHandlerService);
        inOrder.verify(travelPointRepositoryService, times(1)).getAllTravelPointCoordinates();
        inOrder.verify(exceptionHandlerService, times(0)).handleException(any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_getBoxFromAllStops_returns_no_box_when_exception_is_thrown() {
        when(travelPointRepositoryService.getAllTravelPointCoordinates()).thenThrow(new HazelcastException());

        Mono<Box> result = classUnderTest.getBoxFromAllStops();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleException(any(Throwable.class));
    }

}
