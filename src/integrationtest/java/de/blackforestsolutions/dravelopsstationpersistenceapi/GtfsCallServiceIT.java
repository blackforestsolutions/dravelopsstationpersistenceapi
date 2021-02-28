package de.blackforestsolutions.dravelopsstationpersistenceapi;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.GtfsTestConfiguration;
import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import(GtfsTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GtfsCallServiceIT {

    @Autowired
    private ApiToken.ApiTokenBuilder gtfsApiTokenIT;

    @Autowired
    private CallService classUnderTest;

    @Test
    void test_download_gtfs_file() {
        ApiToken testData = gtfsApiTokenIT.build();

        File result = classUnderTest.getFile(testData.getGtfsUrl(), convertToHeaders(testData.getHeaders()));

        assertThat(hasZipFileExtension(result)).isTrue();
        assertThat(convertToZipFile(result)).isInstanceOf(ZipFile.class);
        assertThat(convertToZipFile(result).getEntry("stops.txt")).isInstanceOf(ZipEntry.class);
        assertThat(convertToZipFile(result).getEntry("stops.txt").getSize()).isGreaterThan(0L);
    }
}
