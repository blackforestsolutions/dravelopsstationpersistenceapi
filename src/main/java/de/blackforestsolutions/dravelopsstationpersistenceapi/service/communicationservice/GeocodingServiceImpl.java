package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@RefreshScope
@Service
public class GeocodingServiceImpl implements GeocodingService {

    private static final String METRES_COORDINATE_SYSTEM = "AUTO:42001";
    private static final int NO_BUFFER_IN_METRES = 0;
    private static final int FIRST_INDEX = 0;

    @Value("${graphql.playground.tabs.OPERATING_AREA.bufferInMetres}")
    private int bufferInMetres;

    private final TravelPointRepositoryService travelPointRepositoryService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final GeometryFactory geometryFactory;

    @Autowired
    public GeocodingServiceImpl(TravelPointRepositoryService travelPointRepositoryService, ExceptionHandlerService exceptionHandlerService, GeometryFactory geometryFactory) {
        this.travelPointRepositoryService = travelPointRepositoryService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.geometryFactory = geometryFactory;
    }

    @Override
    public Mono<Polygon> getPolygonFromAllStops() {
        try {
            Polygon polygon = calculatePolygon();
            return Mono.just(polygon)
                    .onErrorResume(exceptionHandlerService::handleException);
        } catch (FactoryException | RuntimeException | TransformException e) {
            return exceptionHandlerService.handleException(e);
        }
    }

    @Override
    public Mono<Box> getBoxFromAllStops() {
        try {
            Polygon polygon = calculatePolygon();
            return Mono.just(convertPolygonToBox(polygon))
                    .onErrorResume(exceptionHandlerService::handleException);
        } catch (FactoryException | RuntimeException | TransformException e) {
            return exceptionHandlerService.handleException(e);
        }
    }

    private Polygon calculatePolygon() throws FactoryException, TransformException {
        List<Point> databasePoints = travelPointRepositoryService.getAllTravelPointCoordinates();
        Geometry[] points = convertPointsToGeometries(databasePoints);
        GeometryCollection geometryCollection = new GeometryCollection(points, geometryFactory);
        Geometry polygon = new ConvexHull(geometryCollection).getConvexHull();
        return (Polygon) bufferPolygon(polygon);
    }

    private Box convertPolygonToBox(Polygon polygon) {
        double leftTopLat = polygon.getExteriorRing().getCoordinateN(FIRST_INDEX).getY();
        double leftTopLon = polygon.getExteriorRing().getCoordinateN(FIRST_INDEX).getX();
        double rightBottomLat = polygon.getExteriorRing().getCoordinateN(FIRST_INDEX).getY();
        double rightBottomLon = polygon.getExteriorRing().getCoordinateN(FIRST_INDEX).getX();

        for (Coordinate coordinate : polygon.getExteriorRing().getCoordinates()) {
            if (coordinate.getY() > leftTopLat) {
                leftTopLat = coordinate.getY();
            }
            if (coordinate.getY() < rightBottomLat) {
                rightBottomLat = coordinate.getY();
            }
            if (coordinate.getX() < leftTopLon) {
                leftTopLon = coordinate.getX();
            }
            if (coordinate.getX() > rightBottomLon) {
                rightBottomLon = coordinate.getX();
            }
        }

        return convertToBox(leftTopLat, leftTopLon, rightBottomLat, rightBottomLon);
    }

    private Box convertToBox(double leftTopLat, double leftTopLon, double rightBottomLat, double rightBottomLon) {
        Point leftTop = new Point.PointBuilder(leftTopLon, leftTopLat).build();
        Point rightBottom = new Point.PointBuilder(rightBottomLon, rightBottomLat).build();
        return new Box.BoxBuilder(leftTop, rightBottom).build();
    }

    private Geometry[] convertPointsToGeometries(List<Point> points) {
        return points.stream()
                .map(point -> new Coordinate(point.getX(), point.getY()))
                .map(geometryFactory::createPoint)
                .toArray(org.locationtech.jts.geom.Point[]::new);
    }

    /**
     * https://stackoverflow.com/questions/36455020/geotools-bounding-box-for-a-buffer-in-wgs84
     * @param sourcePolygon surrounding stations
     * @return buffered polygon
     * @throws FactoryException when factory for transform coordinate systems is not available
     * @throws TransformException when transformation between both coordinate systems is not possible
     */
    private Geometry bufferPolygon(Geometry sourcePolygon) throws FactoryException, TransformException {
        if (bufferInMetres == NO_BUFFER_IN_METRES) {
            return sourcePolygon;
        }
        String completeCrsId = buildMetresCoordinateSystemWith(sourcePolygon);
        CoordinateReferenceSystem metresCRS = CRS.decode(completeCrsId);

        MathTransform degreesToMetres = CRS.findMathTransform(DefaultGeographicCRS.WGS84, metresCRS);
        MathTransform metresToDegrees = CRS.findMathTransform(metresCRS, DefaultGeographicCRS.WGS84);

        Geometry bufferedPolygon = JTS.transform(sourcePolygon, degreesToMetres).buffer(bufferInMetres);
        return JTS.transform(bufferedPolygon, metresToDegrees);
    }

    private String buildMetresCoordinateSystemWith(Geometry sourcePolygon) {
        return METRES_COORDINATE_SYSTEM
                .concat(",")
                .concat(String.valueOf(sourcePolygon.getCentroid().getCoordinate().x))
                .concat(",")
                .concat(String.valueOf(sourcePolygon.getCentroid().getCoordinate().y));
    }

}
