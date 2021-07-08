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

    private LoService loService;
    private DcpProperties dcpProperties;
    private SubscriptionManagementClient subscriptionManagementClient;
    private SubscriptionTrafficClient subscriptionTrafficClient;
    private ThreadPoolExecutor synchronizingExecutor;
    private DateFormat dateFormat;

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

        QuerySubscriptionsResponse subsctiptions = subscriptionManagementClient
                .getSubsctiptions(dcpProperties.getCustomerNo());
        List<Device> devices = loService.getDevices();

        List<DeviceProperties> devicesProperties = convertToProperties(subsctiptions);

        for (DeviceProperties properites : devicesProperties) {
            Optional<Device> matchingDevice = getMatchingDevice(properites, devices);
            if (matchingDevice.isPresent()) {
                // update
                Device device = matchingDevice.get();
                device.withProperties(properites.toMap());
                synchronizingExecutor.execute(() -> loService.updateDevice(device));
            } else {
                // create
                Device device = new Device() //
                        .withId(MQTT_DEVICES_PREFIX + properites.getSimMsisdn()) //
                        .withName(properites.getSimMsisdn()) //
                        .withProperties(properites.toMap()); //
                synchronizingExecutor.execute(() -> loService.createDevice(device));
            }
        }
        LOG.info("Synchronization in done... ");
    }

    private Optional<Device> getMatchingDevice(DeviceProperties properites, List<Device> devices) {
        for (Device device : devices) {
            if (device.getId().contains(properites.getSimMsisdn())) {
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }

    private List<DeviceProperties> convertToProperties(QuerySubscriptionsResponse subsctiptions) {
        List<DeviceProperties> result = new ArrayList<>();

        for (Subscription subscription : subsctiptions.getSubscriptions().getSubscription()) {
            SimResourceV2 simResourceV2 = subscriptionManagementClient.getSimResource(subscription.getImsi())
                    .getSimResource().get(0); // Must be olny 1
            Traffic traffic = subscriptionTrafficClient.getTraffic(subscription.getImsi()).getTraffic().get(0);// Must
                                                                                                               // be
                                                                                                               // only 1

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