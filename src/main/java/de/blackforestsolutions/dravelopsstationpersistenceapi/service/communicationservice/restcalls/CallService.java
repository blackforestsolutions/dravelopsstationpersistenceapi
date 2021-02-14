package de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.restcalls;

import org.springframework.http.HttpHeaders;

import java.io.File;

public interface CallService {
    File getFile(String url, HttpHeaders headers);
}
