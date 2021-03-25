package de.blackforestsolutions.dravelopsstationpersistenceapi.listener;

import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.TravelPointApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.test.annotation.DirtiesContext;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RefreshScopeListenerMockIT {

    @MockBean
    private TravelPointApiService travelPointApiService;

    @Autowired
    private RefreshEndpoint refreshEndpoint;


    @Test
    void test_refresh_mechanism_updates_all_travelPoints_in_hazelcast() {

        refreshEndpoint.refresh();

        verify(travelPointApiService, times(1)).updateTravelPoints();
    }
}
