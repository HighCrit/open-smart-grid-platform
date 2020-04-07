/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers.GetLightSensorStatusResponseMessageMatcher;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers.MeasurementReportTypeMatcher;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;

public class ResponseSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseSteps.class);

    @Autowired
    @Qualifier("protocolIec60870OutboundOsgpCoreResponsesMessageSender")
    private DeviceResponseMessageSender responseMessageSenderMock;

    @Autowired
    private Iec60870DeviceSteps deviceSteps;

    @Then("I should send a measurement report of type {string} to the platform")
    public void thenIShouldSendMeasurementReportOfType(final String typeId) {
        LOGGER.debug("Then I should send a measurement report of type {}", typeId);

        verify(this.responseMessageSenderMock).send(argThat(new MeasurementReportTypeMatcher(typeId)));
    }

    @Then("^I should send get light sensor status response messages to osgp core$")
    public void iShouldSendGetLightSensorStatusResponseMessagesToOsgpCore(final DataTable dataTable) throws Throwable {
        LOGGER.debug("Then I should send get status response messages to osgp core");

        final List<Map<String, String>> rows = dataTable.asMaps();

        rows.stream().map(m -> this.protocolResponseMessage(m)).forEach(this::verifyResponse);
    }

    private ProtocolResponseMessage protocolResponseMessage(final Map<String, String> map) {
        final String deviceIdentification = map.get("device_identification");
        final Iec60870Device device = this.deviceSteps.getDevice(deviceIdentification).orElse(null);
        final DomainInfo domainInfo = device.getDeviceType().domainType().domainInfo();
        final DeviceMessageMetadata deviceMessageMetadata = DeviceMessageMetadata.newBuilder()
                .withDeviceIdentification(deviceIdentification)
                .withMessageType(MessageType.GET_LIGHT_SENSOR_STATUS.name())
                .build();
        return ProtocolResponseMessage.newBuilder()
                .deviceMessageMetadata(deviceMessageMetadata)
                .domain(domainInfo.getDomain())
                .domainVersion(domainInfo.getDomainVersion())
                .dataObject(new LightSensorStatusDto(map.get("relay_status").equalsIgnoreCase("ON")))
                .result(ResponseMessageResultType.OK)
                .build();
    }

    private void verifyResponse(final ProtocolResponseMessage msg) {
        verify(this.responseMessageSenderMock).send(argThat(new GetLightSensorStatusResponseMessageMatcher(msg)));
    }
}
