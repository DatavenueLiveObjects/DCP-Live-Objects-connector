/*
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.lo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lo")
public class LoProperties {

    private static final int DEFAULT_PAGE_SIZE = 1000;
    private static final String DEFAULT_HOSTNAME = "liveobjects.orange-business.com";
    private static final int DEFAULT_SYNCHRONIZATION_THREAD_POOL_SIZE = 40;

    private String hostname = DEFAULT_HOSTNAME;
    private String apiKey;
    private String deviceGroup;
    private int synchronizationDeviceInterval;
    private int synchronizationThreadPoolSize = DEFAULT_SYNCHRONIZATION_THREAD_POOL_SIZE;
    private int pageSize = DEFAULT_PAGE_SIZE;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(String deviceGroup) {
        this.deviceGroup = deviceGroup;
    }

    public int getSynchronizationDeviceInterval() {
        return synchronizationDeviceInterval;
    }

    public void setSynchronizationDeviceInterval(int synchronizationDeviceInterval) {
        this.synchronizationDeviceInterval = synchronizationDeviceInterval;
    }

    public int getSynchronizationThreadPoolSize() {
        return synchronizationThreadPoolSize;
    }

    public void setSynchronizationThreadPoolSize(int synchronizationThreadPoolSize) {
        this.synchronizationThreadPoolSize = synchronizationThreadPoolSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "LoProperties [hostname=" + hostname + ", apiKey=" + apiKey + ", deviceGroup=" + deviceGroup
                + ", synchronizationDeviceInterval=" + synchronizationDeviceInterval
                + ", synchronizationThreadPoolSize=" + synchronizationThreadPoolSize + ", pageSize=" + pageSize + "]";
    }
}