package com.orange.lo.dcp2lo.lo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.GetDevicesFilter;
import com.orange.lo.sdk.rest.devicemanagement.Groups;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import com.orange.lo.sdk.rest.model.Device;
import com.orange.lo.sdk.rest.model.Group;

import net.jodah.failsafe.RetryPolicy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoServiceTest {

    public static final String NODE_ID = "N0D3ID1";
    public static final String NODE_NAME = "N0D3ID1-name";
    private static final String GROUP_ID = "0ZWkDm";

    @Mock
    private LOApiClient loApiClient;

    @Mock
    private DeviceManagement deviceManagement;
    @Mock
    private Groups groups;
    @Mock
    private Inventory inventory;
    @Mock
    private LoProperties loProperties;
    @Mock
    private DataManagementExtConnector dataManagementExtConnector;

    private LoService loService;

    @BeforeEach
    void setUp() {
        initMocks();

        loProperties = new LoProperties();
        loProperties.setPageSize(20);
        loProperties.setDeviceGroup("dcp");

        loService = new LoService(loApiClient, loProperties, new RetryPolicy<>(), new RetryPolicy<>());
    }

    @Test
    void shouldCreateDevice() {
        // given
        Device device = new Device().withId(NODE_ID).withName(NODE_NAME);

        // when
        loService.createDevice(device);

        // then
        verify(inventory, times(1)).createDevice(device);
        verify(groups, times(1)).getGroups();
    }

    @Test
    void shouldUpdateDevice() {
        // given
        Device device = new Device().withId(NODE_ID).withName(NODE_NAME);

        // when
        loService.updateDevice(device);

        // then
        verify(inventory, times(1)).updateDevice(device);
        verify(groups, times(1)).getGroups();
    }

    @Test
    void shouldCallInventoryOnceWhenNumberOfReturnedDevicesIsLessToPageSize() {
        Device device = new Device();
        List<Device> devices = Collections.nCopies(5, device);
        when(inventory.getDevices(argThat(f -> f.getOffset().equals(0)))).thenReturn(devices);

        loService.getDevices();
        verify(inventory, times(1)).getDevices(any(GetDevicesFilter.class));
    }

    @Test
    void shouldCallInventoryTwiceWhenNumberOfReturnedDevicesIsEqualToPageSize() {
        Device device = new Device();
        List<Device> devices = Collections.nCopies(loProperties.getPageSize(), device);
        when(inventory.getDevices(argThat(f -> f.getOffset().equals(0)))).thenReturn(devices);

        loService.getDevices();

        verify(inventory, times(2)).getDevices(any(GetDevicesFilter.class));
    }

    private void initMocks() {
        when(loApiClient.getDataManagementExtConnector()).thenReturn(dataManagementExtConnector);
        when(loApiClient.getDeviceManagement()).thenReturn(deviceManagement);
        when(deviceManagement.getInventory()).thenReturn(inventory);
        when(deviceManagement.getGroups()).thenReturn(groups);
        when(groups.getGroups()).thenReturn(new ArrayList<>());
        Group group = new Group().withId(GROUP_ID).withPathNode(loProperties.getDeviceGroup());
        when(groups.createGroup(anyString())).thenReturn(group);
    }
}