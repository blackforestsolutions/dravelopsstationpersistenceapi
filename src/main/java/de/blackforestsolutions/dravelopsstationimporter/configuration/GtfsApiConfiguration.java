package de.blackforestsolutions.dravelopsstationimporter.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootConfiguration
public class GtfsApiConfiguration {

    @Bean(name = "gtfsApiTokensBuilder")
    @ConfigurationProperties(prefix = "files.apitokens")
    public List<ApiToken.ApiTokenBuilder> apiTokens() {
        return new LinkedList<>();
    }

    @Bean(name = "gtfsApiTokens")
    public List<ApiToken> apiTokens(List<ApiToken.ApiTokenBuilder> gtfsApiTokensBuilder) {
        return gtfsApiTokensBuilder
                .stream()
                .map(ApiToken.ApiTokenBuilder::build)
                .collect(Collectors.toList());
    }
}
