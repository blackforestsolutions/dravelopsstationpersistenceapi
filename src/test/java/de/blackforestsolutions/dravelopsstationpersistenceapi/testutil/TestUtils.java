package de.blackforestsolutions.dravelopsstationpersistenceapi.testutil;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.GtfsApiTokenConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TestUtils {

    /**
     * converts configToken to apiToken
     *
     * @param apiTokens from dravelopsdatamodel repo
     * @return GtfsApiTokenConfiguration
     */
    public static GtfsApiTokenConfiguration convertApiTokensToConfigToken(List<ApiToken> apiTokens) {
        GtfsApiTokenConfiguration gtfsApiTokenConfiguration = new GtfsApiTokenConfiguration();
        gtfsApiTokenConfiguration.setApitokens(apiTokens.stream()
                .map(TestUtils::convertApiTokenToConfigToken)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        return gtfsApiTokenConfiguration;
    }

    private static Map.Entry<String, GtfsApiTokenConfiguration.ApiToken> convertApiTokenToConfigToken(ApiToken apiToken) {
        GtfsApiTokenConfiguration.ApiToken configToken = new GtfsApiTokenConfiguration.ApiToken();
        configToken.setGtfsUrl(apiToken.getGtfsUrl());
        configToken.setHeaders(apiToken.getHeaders());
        return Map.entry(apiToken.getGtfsProvider(), configToken);
    }

}
