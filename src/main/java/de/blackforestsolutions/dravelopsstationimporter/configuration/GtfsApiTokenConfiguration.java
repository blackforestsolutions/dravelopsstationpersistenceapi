package de.blackforestsolutions.dravelopsstationimporter.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "files")
public class GtfsApiTokenConfiguration {

    private List<ApiToken> apitokens;

    @Setter
    @Getter
    public static class ApiToken {

        private String protocol;
        private String host;
        private String path;
        private Map<String, String> headers;
    }

}
