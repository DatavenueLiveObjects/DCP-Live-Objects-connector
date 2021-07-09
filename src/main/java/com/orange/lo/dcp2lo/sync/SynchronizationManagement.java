/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.sync;

import static com.orange.lo.sdk.rest.devicemanagement.Inventory.MQTT_DEVICES_PREFIX;

import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.orange.lo.dcp2lo.dcp.CodeConverter;
import com.orange.lo.dcp2lo.dcp.DcpProperties;
import com.orange.lo.dcp2lo.dcp.SubscriptionManagementClient;
import com.orange.lo.dcp2lo.dcp.SubscriptionTrafficClient;
import com.orange.lo.dcp2lo.lo.LoService;
import com.orange.lo.sdk.rest.model.Device;

import net.ericsson.dcp.api.subscriptionmanagement.QuerySubscriptionsResponse;
import net.ericsson.dcp.api.subscriptionmanagement.SimResourceV2;
import net.ericsson.dcp.api.subscriptionmanagement.Subscription;
import net.ericsson.dcp.api.subscriptiontraffic.Traffic;

@EnableScheduling
@Component
public class SynchronizationManagement {

    private static final String DATA_TYPE = "Data";
    private static final String SMS_TYPE = "SMS";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LoService loService;
    private final DcpProperties dcpProperties;
    private final SubscriptionManagementClient subscriptionManagementClient;
    private final SubscriptionTrafficClient subscriptionTrafficClient;
    private final ThreadPoolExecutor synchronizingExecutor;
    private final DateFormat dateFormat;

    public SynchronizationManagement(ThreadPoolExecutor synchronizingExecutor, LoService loService,
            DcpProperties dcpProperties, SubscriptionManagementClient subscriptionManagementClient,
            SubscriptionTrafficClient subscriptionTrafficClient, DateFormat dateFormat) {
        this.synchronizingExecutor = synchronizingExecutor;
        this.loService = loService;
        this.dcpProperties = dcpProperties;
        this.subscriptionManagementClient = subscriptionManagementClient;
        this.subscriptionTrafficClient = subscriptionTrafficClient;
        this.dateFormat = dateFormat;
    }

    @Scheduled(fixedRateString = "${lo.synchronization-interval}")
    public void synchronize() {
        LOG.info("Synchronization in progress... ");

        QuerySubscriptionsResponse subscriptions = subscriptionManagementClient
                .getSubscriptions(dcpProperties.getCustomerNo());
        List<Device> devices = loService.getDevices();

        List<DeviceProperties> devicesProperties = convertToProperties(subscriptions);

        for (DeviceProperties properties : devicesProperties) {
            Optional<Device> matchingDevice = getMatchingDevice(properties, devices);
            if (matchingDevice.isPresent()) {
                // update
                Device device = matchingDevice.get();
                device.withProperties(properties.toMap());
                synchronizingExecutor.execute(() -> loService.updateDevice(device));
            } else {
                // create
                Device device = new Device() //
                        .withId(MQTT_DEVICES_PREFIX + properties.getSimMsisdn()) //
                        .withName(properties.getSimMsisdn()) //
                        .withProperties(properties.toMap()); //
                synchronizingExecutor.execute(() -> loService.createDevice(device));
            }
        }
        LOG.info("Synchronization in done... ");
    }

    private Optional<Device> getMatchingDevice(DeviceProperties properties, List<Device> devices) {
        for (Device device : devices) {
            if (device.getId().contains(properties.getSimMsisdn())) {
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }

    private List<DeviceProperties> convertToProperties(QuerySubscriptionsResponse subscriptions) {
        List<DeviceProperties> result = new ArrayList<>();

        for (Subscription subscription : subscriptions.getSubscriptions().getSubscription()) {
            // Must be exactly 1
            SimResourceV2 simResourceV2 = subscriptionManagementClient.getSimResource(subscription.getImsi())
                    .getSimResource().get(0);
            // Must be exactly 1
            Traffic traffic = subscriptionTrafficClient.getTraffic(subscription.getImsi()).getTraffic().get(0);

            DeviceProperties deviceProperties = new DeviceProperties(dateFormat);

            deviceProperties.setNetworkLastUpdate(subscription.getLastNetworkActivity());
            deviceProperties.setNetworkCountry(subscription.getLastCountry());
            deviceProperties.setNetworkOperator(subscription.getLastOperator());
            deviceProperties.setNetworkLastInteraction(getLastInteraction(subscription));

            deviceProperties.setSimcardId(CodeConverter.iccidToNsce(simResourceV2.getIcc()));
            deviceProperties.setSimIccid(simResourceV2.getIcc());
            deviceProperties.setSimImei(simResourceV2.getImei());
            deviceProperties.setSimLastUpdate(subscription.getLastNetworkActivity());
            deviceProperties.setSimMsisdn(simResourceV2.getMsisdn());
            deviceProperties.setSimStatus(simResourceV2.getSimSubscriptionStatus().name());
            deviceProperties.setSimImsi(subscription.getImsi());

            deviceProperties.setTrafficCounterStartDate(traffic.getSessionStart());
            deviceProperties.setTrafficLastUpdate(traffic.getLastActivity());
            deviceProperties.setTrafficSmsOut(traffic.getSmsMo().getCount());
            deviceProperties.setTrafficVolumeIn(traffic.getGprs().getRx());
            deviceProperties.setTrafficVolumeOut(traffic.getGprs().getTx());
            result.add(deviceProperties);
        }
        return result;
    }

    private String getLastInteraction(Subscription subscription) {
        XMLGregorianCalendar lastData = subscription.getLastData();
        XMLGregorianCalendar lastSMS = subscription.getLastSMS();

        if (lastData == null && lastSMS == null) {
            return "";
        } else if (lastData == null) {
            return SMS_TYPE;
        } else if (lastSMS == null) {
            return DATA_TYPE;
        } else {
            return lastData.toGregorianCalendar().after(lastSMS.toGregorianCalendar()) ? DATA_TYPE : SMS_TYPE;
        }
    }
}
