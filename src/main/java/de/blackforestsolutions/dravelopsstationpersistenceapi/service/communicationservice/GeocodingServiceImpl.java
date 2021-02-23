package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    private static final String DEGREES_COORDINATE_SYSTEM = "EPSG:4326";
    private static final String METRES_COORDINATE_SYSTEM = "EPSG:32630";
    private static final int NO_BUFFER_IN_METRES = 0;
    private static final int FIRST_INDEX = 0;

    private final TravelPointRepositoryService travelPointRepositoryService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final GeometryFactory geometryFactory;
    private final int bufferInMetres;

    @Autowired
    public GeocodingServiceImpl(TravelPointRepositoryService travelPointRepositoryService, ExceptionHandlerService exceptionHandlerService, GeometryFactory geometryFactory, int bufferInMetres) {
        this.travelPointRepositoryService = travelPointRepositoryService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.geometryFactory = geometryFactory;
        this.bufferInMetres = bufferInMetres;
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

    private Geometry bufferPolygon(Geometry sourcePolygon) throws FactoryException, TransformException {
        if (bufferInMetres == NO_BUFFER_IN_METRES) {
            return sourcePolygon;
        }
        CoordinateReferenceSystem degreesCRS = CRS.decode(DEGREES_COORDINATE_SYSTEM);
        CoordinateReferenceSystem metresCRS = CRS.decode(METRES_COORDINATE_SYSTEM);

        MathTransform degreesToMetres = CRS.findMathTransform(degreesCRS, metresCRS);
        MathTransform metresToDegrees = CRS.findMathTransform(metresCRS, degreesCRS);

        Geometry bufferedPolygon = JTS.transform(sourcePolygon, degreesToMetres).buffer(bufferInMetres);
        return JTS.transform(bufferedPolygon, metresToDegrees);
    }


}
