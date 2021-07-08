/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.dcp;

import javax.xml.bind.JAXBElement;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import net.ericsson.dcp.api.subscriptiontraffic.ObjectFactory;
import net.ericsson.dcp.api.subscriptiontraffic.Query;
import net.ericsson.dcp.api.subscriptiontraffic.QueryResponse;
import net.ericsson.dcp.api.subscriptiontraffic.Resource;
import net.ericsson.dcp.api.subscriptiontraffic.ResourceType;

public class SubscriptionTrafficClient extends WebServiceGatewaySupport {

    private ObjectFactory trafficObjectFactory;

    public SubscriptionTrafficClient(ObjectFactory trafficObjectFactory) {
        this.trafficObjectFactory = trafficObjectFactory;
    }

    public QueryResponse getTraffic(String imsi) {

        Query req = new Query();
        Resource resource = new Resource();
        resource.setId(imsi);
        resource.setType(ResourceType.IMSI);
        req.setResource(resource);

        JAXBElement<Query> createQuery = trafficObjectFactory.createQuery(req);

        javax.xml.bind.JAXBElement<QueryResponse> res = (javax.xml.bind.JAXBElement<QueryResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(createQuery);

        return res.getValue();
    }
}