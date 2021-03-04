package de.blackforestsolutions.dravelopsstationpersistenceapi.configuration;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.repositoryservice.projection.PointProjection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.util.Assert;

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
        // spring geo
        clientUserCodeDeploymentConfig.addClass(Distance.class);
        clientUserCodeDeploymentConfig.addClass(Metric.class);
        clientUserCodeDeploymentConfig.addClass(Range.class);
        clientUserCodeDeploymentConfig.addClass(Metrics.class);
        clientUserCodeDeploymentConfig.addClass(Assert.class);
        // projections
        clientUserCodeDeploymentConfig.addClass(PointProjection.class);
        // datamodel
        clientUserCodeDeploymentConfig.addClass(TravelPoint.class);
        clientUserCodeDeploymentConfig.addClass(TravelPoint.TravelPointBuilder.class);
        clientUserCodeDeploymentConfig.addClass(Point.class);
        clientUserCodeDeploymentConfig.addClass(Point.PointBuilder.class);

        clientUserCodeDeploymentConfig.setEnabled(true);
        return clientUserCodeDeploymentConfig;
    }

}
