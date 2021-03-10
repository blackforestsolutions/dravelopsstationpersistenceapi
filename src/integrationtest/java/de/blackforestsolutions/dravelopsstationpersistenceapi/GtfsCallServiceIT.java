package de.blackforestsolutions.dravelopsstationpersistenceapi;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.GtfsApiTokenConfiguration;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.*;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.testutil.TestUtils.convertConfigTokenToApiTokens;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GtfsCallServiceIT {

    @Autowired
    private GtfsApiTokenConfiguration apiTokenConfiguration;

    @Autowired
    private CallService classUnderTest;

    @Test
    void test_download_gtfs_file() {
        ApiToken testData = convertConfigTokenToApiTokens(apiTokenConfiguration).get(0);

        File result = classUnderTest.getFile(testData.getGtfsUrl(), convertToHeaders(testData.getHeaders()));

        assertThat(hasZipFileExtension(result)).isTrue();
        assertThat(convertToZipFile(result)).isInstanceOf(ZipFile.class);
        assertThat(convertToZipFile(result).getEntry("stops.txt")).isInstanceOf(ZipEntry.class);
        assertThat(convertToZipFile(result).getEntry("stops.txt").getSize()).isGreaterThan(0L);
    }
}
