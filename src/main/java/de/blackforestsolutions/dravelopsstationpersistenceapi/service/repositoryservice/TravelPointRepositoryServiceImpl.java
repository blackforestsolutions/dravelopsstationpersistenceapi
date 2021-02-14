package de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.HAZELCAST_INSTANCE;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.HazelcastConfiguration.TRAVEL_POINT_MAP;

@Service
@Slf4j
public class TravelPointRepositoryServiceImpl implements TravelPointRepositoryService {

    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public TravelPointRepositoryServiceImpl(@Qualifier(HAZELCAST_INSTANCE) HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Collection<TravelPoint> getAllTravelPoints() {
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastInstance.getMap(TRAVEL_POINT_MAP);
        return hazelcastTravelPoints.values();
    }

    @Override
    public Collection<TravelPoint> replaceAllTravelPoints(Map<UUID, TravelPoint> travelPoints) {
        IMap<UUID, TravelPoint> hazelcastTravelPoints = hazelcastInstance.getMap(TRAVEL_POINT_MAP);

        hazelcastTravelPoints.clear();
        log.info("All TravelPoints are successfully removed from database.");

        hazelcastTravelPoints.putAll(travelPoints);
        log.info(hazelcastTravelPoints.size() + " TravelPoints are successfully imported to database.");

        return travelPoints.values();
    }
}
