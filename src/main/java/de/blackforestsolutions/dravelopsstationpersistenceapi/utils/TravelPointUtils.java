package de.blackforestsolutions.dravelopsstationpersistenceapi.utils;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Slf4j
public class TravelPointUtils {

    /**
     * This method distinct travelPoints with the same name, platforms and coordinates
     * FE_FLOATING_POINT_EQUALITY warning indicates an overflow error for a double.
     * This warning is suppressed as we dont want a tolerance here
     *
     * @return Predicate to distinct equal TravelPoints
     */
    public static Predicate<TravelPoint> distinctEquivalentTravelPoints() {
        Set<TravelPoint> savedTravelPoints = ConcurrentHashMap.newKeySet();
        return t -> {
            for (TravelPoint savedTravelPoint : savedTravelPoints) {

                if (savedTravelPoint.getName().equals(t.getName()) && savedTravelPoint.getPoint().getX() == t.getPoint().getX() && savedTravelPoint.getPoint().getY() == t.getPoint().getY() && savedTravelPoint.getPlatform().equals(t.getPlatform())) {
                    log.info(t.getName().concat(" is sorted out because it is already downloaded"));
                    return false;
                }
            }
            return savedTravelPoints.add(t);
        };
    }
}
