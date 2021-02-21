package de.blackforestsolutions.dravelopsstationpersistenceapi.controller;

import de.blackforestsolutions.dravelopsstationpersistenceapi.service.communicationservice.PolygonApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("polygons/get")
public class PolygonController {

    private final PolygonApiService polygonApiService;
}
