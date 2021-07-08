/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.dcp;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

@Component
public class SecurityInterceptor extends ClientInterceptorAdapter {

    private static final String SECURITY_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String HEADER_NAME = "Security";
    private static final String HEADER_PREFIX = "wsse";
    private static final String USERNAME_TOKEN_NAME = "UsernameToken";
    private static final String USERNAME_TOKEN_PREFIX = "wsse";
    private static final String USERNAME_NAME = "Username";
    private static final String USERNAME_PREFIX = "wsse";
    private static final String PASSWORD_NAME = "Password";
    private static final String PASSWORD_PREFIX = "wsse";

    private DcpProperties dcpProperties;

    public SecurityInterceptor(DcpProperties dcpProperties) {
        this.dcpProperties = dcpProperties;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) messageContext.getRequest();

        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();

        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope soapEnvelope = null;
        try {
            soapEnvelope = soapPart.getEnvelope();
            SOAPHeader soapHeader = soapEnvelope.getHeader();

            Name headerElementName = soapEnvelope.createName(HEADER_NAME, HEADER_PREFIX, SECURITY_URI);
            SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement(headerElementName);

            SOAPElement usernameTokenSOAPElement = soapHeaderElement.addChildElement(USERNAME_TOKEN_NAME,
                    USERNAME_TOKEN_PREFIX);

            SOAPElement userNameSOAPElement = usernameTokenSOAPElement.addChildElement(USERNAME_NAME, USERNAME_PREFIX);
            userNameSOAPElement.setTextContent(dcpProperties.getLogin());

            SOAPElement passwordSOAPElement = usernameTokenSOAPElement.addChildElement(PASSWORD_NAME, PASSWORD_PREFIX);
            passwordSOAPElement.setTextContent(dcpProperties.getPassword());

        } catch (SOAPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}