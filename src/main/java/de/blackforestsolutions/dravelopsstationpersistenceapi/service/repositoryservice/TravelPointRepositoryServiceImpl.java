package de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.projection.PointProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.HAZELCAST_INSTANCE;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.TRAVEL_POINT_MAP;

@Service
@Slf4j
public class TravelPointRepositoryServiceImpl implements TravelPointRepositoryService {

    private final IMap<UUID, TravelPoint> hazelcastTravelPoints;

    @Autowired
    public TravelPointRepositoryServiceImpl(@Qualifier(HAZELCAST_INSTANCE) HazelcastInstance hazelcastInstance) {
        this.hazelcastTravelPoints = hazelcastInstance.getMap(TRAVEL_POINT_MAP);
    }

    @Override
    public List<TravelPoint> getAllTravelPoints() {
        return new ArrayList<>(hazelcastTravelPoints.values());
    }

    @Override
    public List<Point> getAllTravelPointCoordinates() {
        return new ArrayList<>(hazelcastTravelPoints.project(new PointProjection()));
    }

    @Override
    public List<TravelPoint> replaceAllTravelPoints(Map<UUID, TravelPoint> travelPoints) {
        hazelcastTravelPoints.clear();
        log.info("All TravelPoints are removed from database.");

        hazelcastTravelPoints.putAll(travelPoints);
        log.info(hazelcastTravelPoints.size() + " TravelPoints are imported to database.");

        return new ArrayList<>(travelPoints.values());
    }
}
