package de.blackforestsolutions.dravelopsstationpersistenceapi.listener;

import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.TravelPointApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RefreshScopeListener implements ApplicationListener<RefreshScopeRefreshedEvent> {

    private final TravelPointApiService travelPointApiService;

    @Autowired
    public RefreshScopeListener(TravelPointApiService travelPointApiService) {
        this.travelPointApiService = travelPointApiService;
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        travelPointApiService.updateTravelPoints();
    }
}
