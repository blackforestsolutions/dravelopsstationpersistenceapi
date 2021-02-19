package de.blackforestsolutions.dravelopsstationpersistenceapi.service.supportservice;

import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.internal.Strings;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.regex.Pattern;

class UuidServiceTest {

    private final UuidService classUnderTest = new UuidServiceImpl();

    @Test
    void test_createUUID_returns_correct_uuid_pattern() {
        UUID testData = classUnderTest.createUUID();
        Pattern checkPattern = Pattern.compile("([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})");

        Strings.instance().assertContainsPattern(new WritableAssertionInfo(), testData.toString(), checkPattern);
    }
}
