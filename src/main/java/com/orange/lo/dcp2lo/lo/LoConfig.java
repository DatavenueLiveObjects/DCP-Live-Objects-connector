/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.lo;

import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.rest.model.Device;
import com.orange.lo.sdk.rest.model.Group;

import net.jodah.failsafe.RetryPolicy;

@Configuration
public class LoConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LoProperties loProperties;

    public LoConfig(LoProperties loProperties) {
        this.loProperties = loProperties;
    }

    @Bean
    ThreadPoolExecutor synchronizingExecutor() {
        return new ThreadPoolExecutor(loProperties.getSynchronizationThreadPoolSize(), //
                loProperties.getSynchronizationThreadPoolSize(), //
                10, //
                TimeUnit.SECONDS, //
                new LinkedBlockingQueue<>()); //
    }

    @Bean
    public LOApiClient loApiClient() {
        LOGGER.debug("Initializing LOApiClient");
        LOApiClientParameters parameters = loApiClientParameters();
        return new LOApiClient(parameters);
    }

    LOApiClientParameters loApiClientParameters() {
        return LOApiClientParameters.builder() //
                .hostname(loProperties.getHostname()) //
                .apiKey(loProperties.getApiKey()) //
                .build();
    }

    @Bean
    public RetryPolicy<List<Device>> restDevicesRetryPolicy() {
        return new RetryPolicy<List<Device>>()
                .handleIf(e -> e instanceof HttpClientErrorException
                        && ((HttpClientErrorException) e).getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) //
                .withMaxAttempts(-1) //
                .withBackoff(1, 60, ChronoUnit.SECONDS) //
                .withMaxDuration(Duration.ofHours(1)); //
    }

    @Bean
    public RetryPolicy<Group> restGroupRetryPolicy() {
        return new RetryPolicy<Group>()
                .handleIf(e -> e instanceof HttpClientErrorException
                        && ((HttpClientErrorException) e).getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) //
                .withMaxAttempts(-1) //
                .withBackoff(1, 60, ChronoUnit.SECONDS) //
                .withMaxDuration(Duration.ofHours(1)); //
    }

    @Bean
    public DateFormat dateFormat() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }
}
