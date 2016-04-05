/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.endpointinterceptors;

import org.springframework.util.Assert;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

/**
 * Intercept a SOAP Header and put the message schedule time contents in the
 * MessageContext.
 */
public class SoapHeaderScheduleTimeEndpointInterceptor implements EndpointInterceptor {

    private final String scheduleTimeHeaderName;

    public SoapHeaderScheduleTimeEndpointInterceptor(final String scheduleTimeHeaderName) {
        this.scheduleTimeHeaderName = scheduleTimeHeaderName;
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {

        Assert.isInstanceOf(SoapMessage.class, messageContext.getRequest());
        final SoapMessage request = (SoapMessage) messageContext.getRequest();
        final SoapHeader soapHeader = request.getSoapHeader();

        // Try to get the value from the Soap Header.
        final String scheduleTime = SoapHeaderEndpointInterceptorHelper.getHeaderValue(soapHeader,
                this.scheduleTimeHeaderName);

        // Finally, set the message schedule time into the message
        // context, so it can be used in the end point later.
        messageContext.setProperty(this.scheduleTimeHeaderName, scheduleTime);

        // Return true so the interceptor chain will continue.
        return true;
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
        return true;
    }

    @Override
    public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex)
            throws Exception {
        // Empty Method
    }
}
