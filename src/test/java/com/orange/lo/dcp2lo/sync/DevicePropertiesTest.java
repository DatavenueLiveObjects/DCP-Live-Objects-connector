package com.orange.lo.dcp2lo.sync;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DevicePropertiesTest {

    @Test
    void toMapResultHasAtMost10Elements() {
        DeviceProperties deviceProperties = new DeviceProperties(DateFormat.getInstance());

        Map<String, String> map = deviceProperties.toMap();

        assertTrue(map.size() <= 10);
    }
}
