package de.blackforestsolutions.dravelopsstationimporter.service.repositoryservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface TravelPointRepositoryService {
    Collection<TravelPoint> getAllTravelPoints();

    Collection<TravelPoint> replaceAllTravelPoints(Map<UUID, TravelPoint> travelPoints);
}
