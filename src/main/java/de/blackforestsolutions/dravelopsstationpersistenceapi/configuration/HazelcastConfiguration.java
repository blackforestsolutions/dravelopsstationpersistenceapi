package de.blackforestsolutions.dravelopsstationpersistenceapi.configuration;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootConfiguration
public class HazelcastConfiguration {

    public static final String TRAVEL_POINT_MAP = "travel-point-map";
    public static final String HAZELCAST_INSTANCE = "hazelcastInstance";

    @Value("${hazelcast.addresses}")
    private List<String> hazelcastAddresses;

    @Bean
    public ClientConfig config() {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().setAddresses(hazelcastAddresses);
        config.setUserCodeDeploymentConfig(userCodeDeploymentConfig());
        return config;
    }

    private ClientUserCodeDeploymentConfig userCodeDeploymentConfig() {
        ClientUserCodeDeploymentConfig clientUserCodeDeploymentConfig = new ClientUserCodeDeploymentConfig();

        clientUserCodeDeploymentConfig.addClass(TravelPoint.class);
        clientUserCodeDeploymentConfig.addClass(TravelPoint.TravelPointBuilder.class);
        clientUserCodeDeploymentConfig.addClass(Point.class);
        clientUserCodeDeploymentConfig.addClass(Point.PointBuilder.class);

        clientUserCodeDeploymentConfig.setEnabled(true);
        return clientUserCodeDeploymentConfig;
    }

}
