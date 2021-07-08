/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.dcp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import net.ericsson.dcp.api.subscriptiontraffic.ObjectFactory;

@Configuration
public class DcpConfig {

    private static final String SUBSCRIPTION_TRAFFIC_URI = "https://trial.dcp.ericsson.net/dcpapi/SubscriptionTraffic";
    private static final String SUBSCRIPTION_MANAGEMENT_URI = "https://trial.dcp.ericsson.net/dcpapi/SubscriptionManagement";
    private static final String SUBSCRIPTION_TRAFFIC_CONTEXT_PATH = "net.ericsson.dcp.api.subscriptiontraffic";
    private static final String SUBSCRIPTION_MANAGEMENT_CONTEXT_PATH = "net.ericsson.dcp.api.subscriptionmanagement";

    private SecurityInterceptor securityInterceptor;

    public DcpConfig(SecurityInterceptor securityInterceptor) {
        this.securityInterceptor = securityInterceptor;
    }

    @Bean
    public Jaxb2Marshaller subscriptionManagementMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(SUBSCRIPTION_MANAGEMENT_CONTEXT_PATH);
        return marshaller;
    }

    @Bean
    public Jaxb2Marshaller subscriptionTrafficMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(SUBSCRIPTION_TRAFFIC_CONTEXT_PATH);
        return marshaller;
    }

    @Bean
    public SubscriptionManagementClient subscriptionManagementClient(Jaxb2Marshaller subscriptionManagementMarshaller) {
        SubscriptionManagementClient subscriptionManagementClient = new SubscriptionManagementClient();
        subscriptionManagementClient.setDefaultUri(SUBSCRIPTION_MANAGEMENT_URI);
        subscriptionManagementClient.setMarshaller(subscriptionManagementMarshaller);
        subscriptionManagementClient.setUnmarshaller(subscriptionManagementMarshaller);
        subscriptionManagementClient.setInterceptors(new ClientInterceptor[] { securityInterceptor });

        return subscriptionManagementClient;
    }

    @Bean
    public ObjectFactory trafficObjectFactory() {
        return new ObjectFactory();
    }

    @Bean
    public SubscriptionTrafficClient subscriptionTrafiicClient(Jaxb2Marshaller subscriptionTrafficMarshaller,
            ObjectFactory trafficObjectFactory) {
        SubscriptionTrafficClient subscriptionTrafficClient = new SubscriptionTrafficClient(trafficObjectFactory);
        subscriptionTrafficClient.setDefaultUri(SUBSCRIPTION_TRAFFIC_URI);
        subscriptionTrafficClient.setMarshaller(subscriptionTrafficMarshaller);
        subscriptionTrafficClient.setUnmarshaller(subscriptionTrafficMarshaller);
        subscriptionTrafficClient.setInterceptors(new ClientInterceptor[] { securityInterceptor });

        return subscriptionTrafficClient;
    }
}
