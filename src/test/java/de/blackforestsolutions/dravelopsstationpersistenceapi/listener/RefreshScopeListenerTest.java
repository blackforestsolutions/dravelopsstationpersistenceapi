package de.blackforestsolutions.dravelopsstationpersistenceapi.listener;

import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.TravelPointApiService;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.TravelPointApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;

import static org.mockito.Mockito.*;

class RefreshScopeListenerTest {

    private final TravelPointApiService travelPointApiService = mock(TravelPointApiServiceImpl.class);

    private final RefreshScopeListener classUnderTest = new RefreshScopeListener(travelPointApiService);

    @Test
    void test_onApplicationEvent_with_refreshScopeEvent_calls_travelPointApiService_once() {
        RefreshScopeRefreshedEvent testData = new RefreshScopeRefreshedEvent();

        classUnderTest.onApplicationEvent(testData);

        verify(travelPointApiService, times(1)).updateTravelPoints();
    }
}
