package com.orange.lo.dcp2lo.sync;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

class DevicePropertiesTest {

    @Test
    void toMapResultHasAtMost10Elements() {
        DeviceProperties deviceProperties = new DeviceProperties(DateFormat.getInstance());

        Map<String, String> map = deviceProperties.toMap();

        assertThat(map.size(), lessThanOrEqualTo(10));
    }
}
