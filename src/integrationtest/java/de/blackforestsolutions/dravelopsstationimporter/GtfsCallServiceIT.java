package de.blackforestsolutions.dravelopsstationimporter;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsstationimporter.configuration.GtfsTestConfiguration;
import de.blackforestsolutions.dravelopsstationimporter.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.*;
import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
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

        File result = classUnderTest.getFile(buildUrlWith(testData).toString(), convertToHeaders(testData.getHeaders()));

        assertThat(hasZipFileExtension(result)).isTrue();
        assertThat(convertToZipFile(result)).isInstanceOf(ZipFile.class);
        assertThat(convertToZipFile(result).getEntry("stops.txt")).isInstanceOf(ZipEntry.class);
        assertThat(convertToZipFile(result).getEntry("stops.txt").getSize()).isGreaterThan(0L);
    }
}
