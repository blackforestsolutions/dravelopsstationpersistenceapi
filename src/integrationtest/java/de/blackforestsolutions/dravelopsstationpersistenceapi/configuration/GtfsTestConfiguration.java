package de.blackforestsolutions.dravelopsstationpersistenceapi.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GtfsTestConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "gtfs.apitokens[0]")
    public ApiToken.ApiTokenBuilder gtfsApiTokenIT() {
        return new ApiToken.ApiTokenBuilder();
    }
}
