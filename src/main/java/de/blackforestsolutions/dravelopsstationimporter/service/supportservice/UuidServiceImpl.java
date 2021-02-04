package de.blackforestsolutions.dravelopsstationimporter.service.supportservice;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidServiceImpl implements UuidService {

    @Override
    public UUID createUUID() {
        return UUID.randomUUID();
    }
}
