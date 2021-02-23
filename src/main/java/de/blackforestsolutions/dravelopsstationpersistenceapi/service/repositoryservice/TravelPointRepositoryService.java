package de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice;

import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TravelPointRepositoryService {
    List<TravelPoint> getAllTravelPoints();

    List<TravelPoint> replaceAllTravelPoints(Map<UUID, TravelPoint> travelPoints);

    List<Point> getAllTravelPointCoordinates();
}
