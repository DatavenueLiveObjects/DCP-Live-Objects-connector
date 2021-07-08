/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.dcp;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import net.ericsson.dcp.api.subscriptionmanagement.QuerySimResourceResponseV2;
import net.ericsson.dcp.api.subscriptionmanagement.QuerySimResourceV2;
import net.ericsson.dcp.api.subscriptionmanagement.QuerySubscriptionPackagesRequestV2;
import net.ericsson.dcp.api.subscriptionmanagement.QuerySubscriptionPackagesResponseV2;
import net.ericsson.dcp.api.subscriptionmanagement.QuerySubscriptionsRequest;
import net.ericsson.dcp.api.subscriptionmanagement.QuerySubscriptionsResponse;
import net.ericsson.dcp.api.subscriptionmanagement.Resource;

public class SubscriptionManagementClient extends WebServiceGatewaySupport {

    private static final String IMSI = "imsi";

    public QuerySubscriptionsResponse getSubsctiptions(String customerNo) {
        QuerySubscriptionsRequest req = new QuerySubscriptionsRequest();
        req.setCustomerNo(customerNo);
        req.setMaxResults(20);

        return (QuerySubscriptionsResponse) getWebServiceTemplate().marshalSendAndReceive(req);
    }

    public QuerySimResourceResponseV2 getSimResource(String imsi) {
        QuerySimResourceV2 req = new QuerySimResourceV2();

        Resource resource = new Resource();
        resource.setId(imsi);
        resource.setType(IMSI);

        req.setResource(resource);

        return (QuerySimResourceResponseV2) getWebServiceTemplate().marshalSendAndReceive(req);
    }

    public QuerySubscriptionPackagesResponseV2 getSubscriptionPackage(String customerNo) {
        QuerySubscriptionPackagesRequestV2 req = new QuerySubscriptionPackagesRequestV2();
        req.setCustomerNo(customerNo);
        return (QuerySubscriptionPackagesResponseV2) getWebServiceTemplate().marshalSendAndReceive(req);
    }
}
