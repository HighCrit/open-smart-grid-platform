/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for sending smart metering request messages to a queue
 *
 */
public class SmartMeteringRequestMessageSender {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringRequestMessageSender.class);

    /**
     * Autowired field for smart metering requests jms template
     */
    @Autowired
    @Qualifier("wsSmartMeteringOutgoingRequestsJmsTemplate")
    private JmsTemplate smartMeteringRequestsJmsTemplate;

    /**
     * Method for sending a request message to the queue
     *
     * @param requestMessage
     *            The SmartMeteringRequestMessage request message to send.
     */
    public void send(final SmartMeteringRequestMessage requestMessage, final int messagePriority) {
        LOGGER.debug("Sending smart metering request message to the queue");

        if (requestMessage.getMessageType() == null) {
            LOGGER.error("MessageType is null");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getOrganisationIdentification())) {
            LOGGER.error("OrganisationIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getDeviceIdentification())) {
            LOGGER.error("DeviceIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getCorrelationUid())) {
            LOGGER.error("CorrelationUid is blank");
            return;
        }

        this.sendMessage(requestMessage, messagePriority);
    }

    /**
     * Method for sending a request message to the public lighting requests
     * queue
     *
     * @param requestMessage
     *            The SmartMeteringRequestMessage request message to send.
     */
    private void sendMessage(final SmartMeteringRequestMessage requestMessage, final int messagePriority) {
        LOGGER.info("Sending message to the smart metering requests queue");

        this.smartMeteringRequestsJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(requestMessage.getRequest());
                objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
                objectMessage.setJMSType(requestMessage.getMessageType().toString());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        requestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        requestMessage.getDeviceIdentification());
                objectMessage.setJMSPriority(messagePriority);
                return objectMessage;
            }
        });
    }
}
