package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.TravelPointRepositoryService;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
public class PolygonApiServiceImpl implements PolygonApiService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private final TravelPointRepositoryService travelPointRepositoryService;
    private final ExceptionHandlerService exceptionHandlerService;

    public PolygonApiServiceImpl(TravelPointRepositoryService travelPointRepositoryService, ExceptionHandlerService exceptionHandlerService) {
        this.travelPointRepositoryService = travelPointRepositoryService;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Override
    public Mono<Polygon> getPolygonFromAllStops() {
        try {
            Polygon polygon = calculatePolygon();
            return Mono.just(polygon)
                    .onErrorResume(exceptionHandlerService::handleException);
        } catch (Exception e) {
            return exceptionHandlerService.handleException(e);
        }
    }

    private Polygon calculatePolygon() {
        Collection<Point> databasePoints = travelPointRepositoryService.getAllTravelPointCoordinates();
        Geometry[] points = convertPointsToGeometries(databasePoints);
        GeometryCollection geometryCollection = new GeometryCollection(points, GEOMETRY_FACTORY);
        return (Polygon) new ConvexHull(geometryCollection)
                .getConvexHull()
                .buffer()
    }

    private Geometry[] convertPointsToGeometries(Collection<Point> points) {
        return points.stream()
                .map(point -> new Coordinate(point.getX(), point.getY()))
                .map(GEOMETRY_FACTORY::createPoint)
                .toArray(org.locationtech.jts.geom.Point[]::new);
    }

}
