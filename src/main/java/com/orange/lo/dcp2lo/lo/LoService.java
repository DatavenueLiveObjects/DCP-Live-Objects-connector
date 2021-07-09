/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.lo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.GetDevicesFilter;
import com.orange.lo.sdk.rest.devicemanagement.Groups;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import com.orange.lo.sdk.rest.model.Device;
import com.orange.lo.sdk.rest.model.Group;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

@Component
public class LoService {

    private String groupId;
    private final LoProperties loProperties;
    private final Inventory inventory;
    private final Groups groups;
    private final RetryPolicy<List<Device>> restDevicesRetryPolicy;
    private final RetryPolicy<Group> restGroupRetryPolicy;

    public LoService(LOApiClient loApiClient, LoProperties loProperties,
            RetryPolicy<List<Device>> restDevicesRetryPolicy, RetryPolicy<Group> restGroupRetryPolicy) {
        this.loProperties = loProperties;
        this.restDevicesRetryPolicy = restDevicesRetryPolicy;
        this.restGroupRetryPolicy = restGroupRetryPolicy;
        DeviceManagement deviceManagement = loApiClient.getDeviceManagement();
        this.inventory = deviceManagement.getInventory();
        this.groups = deviceManagement.getGroups();
        manageGroup();
    }

    public void manageGroup() {
        Group group = Failsafe //
                .with(restGroupRetryPolicy) //
                .get(() -> groups.getGroups().stream() //
                        .filter(g -> loProperties.getDeviceGroup().equals(g.getPathNode())) //
                        .findAny() //
                        .orElseGet( //
                                () -> groups.createGroup(loProperties.getDeviceGroup()) //
                        ) //
                ); //

        groupId = group.getId();
    }

    public void createDevice(Device device) {
        Failsafe.with(restDevicesRetryPolicy).run(() -> {
            Group group = new Group().withId(groupId);
            device.withGroup(group);
            inventory.createDevice(device);
        });
    }

    public void updateDevice(Device device) {
        Failsafe.with(restDevicesRetryPolicy).run(() -> inventory.updateDevice(device));
    }

    public List<Device> getDevices() {
        GetDevicesFilter filter = new GetDevicesFilter().withLimit(loProperties.getPageSize()).withGroupId(groupId);
        List<Device> devices = new ArrayList<>();
        for (int i = 0;; i++) {
            int j = i;
            List<Device> list = Failsafe //
                    .with(restDevicesRetryPolicy) //
                    .get(() -> inventory.getDevices(filter.withOffset(j * loProperties.getPageSize()))); //

            devices.addAll(list);
            if (list.size() < loProperties.getPageSize()) {
                break;
            }
        }
        return devices;
    }
}
