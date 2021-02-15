package de.blackforestsolutions.dravelopsstationpersistenceapi.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "gtfs")
public class GtfsApiTokenConfiguration {

    private List<ApiToken> apitokens;

    @Setter
    @Getter
    public static class ApiToken {

        private String gtfsUrl;
        private Map<String, String> headers;
    }

}
