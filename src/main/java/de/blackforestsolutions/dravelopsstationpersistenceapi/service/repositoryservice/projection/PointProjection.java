package de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.projection;

import com.hazelcast.projection.Projection;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;

import java.util.Map;
import java.util.UUID;

public class PointProjection implements Projection<Map.Entry<UUID, TravelPoint>, Point> {

    private static final long serialVersionUID = 8017881270920708011L;

    @Override
    public Point transform(Map.Entry<UUID, TravelPoint> uuidTravelPointMap) {
        return uuidTravelPointMap.getValue().getPoint();
    }
}
