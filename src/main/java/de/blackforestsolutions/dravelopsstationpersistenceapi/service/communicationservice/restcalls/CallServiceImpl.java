package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.restcalls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.FileConfiguration.TMP_FILE_NAME_PREFIX;
import static de.blackforestsolutions.dravelopsstationpersistenceapi.configuration.FileConfiguration.TMP_FILE_NAME_SUFFIX;

@Service
@Slf4j
public class CallServiceImpl implements CallService {

    private final RestTemplate restTemplate;

    public CallServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public File getFile(String url, HttpHeaders headers) {
        log.info("Downloading file from: ".concat(url));
        return restTemplate.execute(
                url,
                HttpMethod.GET,
                clientHttpRequest -> addHeadersToRequest(clientHttpRequest, headers),
                this::extractFile
        );
    }

    private File extractFile(ClientHttpResponse response) throws IOException {
        File tmp = File.createTempFile(TMP_FILE_NAME_PREFIX, TMP_FILE_NAME_SUFFIX);
        StreamUtils.copy(response.getBody(), new FileOutputStream(tmp));
        log.info("File successfully downloaded.");
        return tmp;
    }

    private void addHeadersToRequest(ClientHttpRequest request, HttpHeaders headers) {
        for (Map.Entry<String, List<String>> o : headers.entrySet()) {
            request.getHeaders().addAll(o.getKey(), o.getValue());
        }
    }


}
