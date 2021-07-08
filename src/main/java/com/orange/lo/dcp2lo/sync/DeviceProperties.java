/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.sync;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.util.Strings;

public class DeviceProperties {

    private DateFormat dateFormat;

    private XMLGregorianCalendar simLastUpdate;
    private String simStatus;
    private String simcardId; // Nsce
    private String simImei;
    private String simMsisdn;
    private String simIccid;
    private String simImsi;

    private XMLGregorianCalendar trafficLastUpdate;
    private XMLGregorianCalendar trafficCounterStartDate;
    private double trafficVolumeTotal;
    private double trafficVolumeOut;
    private double trafficVolumeIn;
    private int trafficSmsOut;

    private XMLGregorianCalendar networkLastUpdate;
    private String networkStatus;
    private String networkCountry;
    private String networkOperator;
    private String networkRadioType;
    private String networkLastInteraction;

    public DeviceProperties(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        // map.put("SIM last update", convertXMLGregorianCalendar(simLastUpdate));
        map.put("SIM status ", simStatus);
        // map.put("SIM card ID", simcardId);
        map.put("SIM IMEI", simImei);
//		map.put("SIM MSISDN", simMsisdn);
        map.put("SIM ICCID", simIccid);
        map.put("SIM IMSI", simImsi);
        map.put("Traffic last update", convertXMLGregorianCalendar(trafficLastUpdate));
        // map.put("Traffic counter start date",
        // convertXMLGregorianCalendar(trafficCounterStartDate));
        // map.put("Traffic volume total", String.valueOf(trafficVolumeTotal));
        map.put("Traffic volume out", String.valueOf(trafficVolumeOut));
        map.put("Traffic volume in", String.valueOf(trafficVolumeIn));
        map.put("Traffic SMS out", String.valueOf(trafficSmsOut));
        // map.put("Network last update",
        // convertXMLGregorianCalendar(networkLastUpdate));
        // map.put("Network status", networkStatus);
        map.put("Network country", networkCountry);
        map.put("Network operator", networkOperator);
        // map.put("Network radio type", networkRadioType);
        // map.put("Network last interaction", networkLastInteraction);

        return map;
    }

    private String convertXMLGregorianCalendar(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return Strings.EMPTY;
        }
        return dateFormat.format(calendar.toGregorianCalendar().getTime());
    }

    public XMLGregorianCalendar getSimLastUpdate() {
        return simLastUpdate;
    }

    public void setSimLastUpdate(XMLGregorianCalendar xmlGregorianCalendar) {
        this.simLastUpdate = xmlGregorianCalendar;
    }

    public String getSimStatus() {
        return simStatus;
    }

    public void setSimStatus(String simStatus) {
        this.simStatus = simStatus;
    }

    public String getSimcardId() {
        return simcardId;
    }

    public void setSimcardId(String simcardId) {
        this.simcardId = simcardId;
    }

    public String getSimImei() {
        return simImei;
    }

    public void setSimImei(String simImei) {
        this.simImei = simImei;
    }

    public String getSimMsisdn() {
        return simMsisdn;
    }

    public void setSimMsisdn(String simMsisdn) {
        this.simMsisdn = simMsisdn;
    }

    public String getSimIccid() {
        return simIccid;
    }

    public void setSimIccid(String simIccid) {
        this.simIccid = simIccid;
    }

    public String getSimImsi() {
        return simImsi;
    }

    public void setSimImsi(String simImsi) {
        this.simImsi = simImsi;
    }

    public XMLGregorianCalendar getTrafficLastUpdate() {
        return trafficLastUpdate;
    }

    public void setTrafficLastUpdate(XMLGregorianCalendar xmlGregorianCalendar) {
        this.trafficLastUpdate = xmlGregorianCalendar;
    }

    public XMLGregorianCalendar getTrafficCounterStartDate() {
        return trafficCounterStartDate;
    }

    public void setTrafficCounterStartDate(XMLGregorianCalendar xmlGregorianCalendar) {
        this.trafficCounterStartDate = xmlGregorianCalendar;
    }

    public Double getTrafficVolumeTotal() {
        return trafficVolumeTotal;
    }

    public Double getTrafficVolumeOut() {
        return trafficVolumeOut;
    }

    public void setTrafficVolumeOut(Double volume) {
        this.trafficVolumeOut = volume;
        this.trafficVolumeTotal += volume != null ? volume : 0;
    }

    public Double getTrafficVolumeIn() {
        return trafficVolumeIn;
    }

    public void setTrafficVolumeIn(Double volume) {
        this.trafficVolumeIn = volume;
        this.trafficVolumeTotal += volume != null ? volume : 0;
    }

    public Integer getTrafficSmsOut() {
        return trafficSmsOut;
    }

    public void setTrafficSmsOut(Integer integer) {
        this.trafficSmsOut = integer;
    }

    public XMLGregorianCalendar getNetworkLastUpdate() {
        return networkLastUpdate;
    }

    public void setNetworkLastUpdate(XMLGregorianCalendar xmlGregorianCalendar) {
        this.networkLastUpdate = xmlGregorianCalendar;
    }

    public String getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(String networkStatus) {
        this.networkStatus = networkStatus;
    }

    public String getNetworkCountry() {
        return networkCountry;
    }

    public void setNetworkCountry(String networkCountry) {
        this.networkCountry = networkCountry;
    }

    public String getNetworkOperator() {
        return networkOperator;
    }

    public void setNetworkOperator(String networkOperator) {
        this.networkOperator = networkOperator;
    }

    public String getNetworkRadioType() {
        return networkRadioType;
    }

    public void setNetworkRadioType(String networkRadioType) {
        this.networkRadioType = networkRadioType;
    }

    public String getNetworkLastInteraction() {
        return networkLastInteraction;
    }

    public void setNetworkLastInteraction(String networkLastInteraction) {
        this.networkLastInteraction = networkLastInteraction;
    }
}
