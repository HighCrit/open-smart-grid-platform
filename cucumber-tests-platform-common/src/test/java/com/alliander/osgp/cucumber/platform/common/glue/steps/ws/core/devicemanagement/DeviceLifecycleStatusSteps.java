/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DeviceLifecycleStatusSteps {

    private static final String PATTERN_RETRY_OPERATION = "retry count= .*, correlationuid= .*";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceLifecycleStatusSteps.class);

    @Autowired
    CoreDeviceManagementClient deviceManagementClient;

    @Autowired
    DeviceRepository DeviceRepository;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @When("^the SetDeviceLifecycleStatus request is received$")
    public void theSetDeviceLifecycleStatusRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final SetDeviceLifecycleStatusRequest request = new SetDeviceLifecycleStatusRequest();
        request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        request.setDeviceLifecycleStatus(
                DeviceLifecycleStatus.valueOf(settings.get(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)));

        final SetDeviceLifecycleStatusAsyncResponse asyncResponse = this.deviceManagementClient
                .setDeviceLifecycleStatus(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the device lifecycle status in the database is$")
    public void theDeviceLifecycleStatusInTheDatabaseIs(final Map<String, String> settings) throws Throwable {
        final SetDeviceLifecycleStatusAsyncRequest asyncRequest = new SetDeviceLifecycleStatusAsyncRequest();
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        asyncRequest.setDeviceId((String) ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        // final SetDeviceLifecycleStatusResponse response =
        // this.deviceManagementClient
        // .getSetDeviceLifecycleStatusResponse(asyncRequest);

        Wait.until(() -> {
            SetDeviceLifecycleStatusResponse response = null;
            try {
                response = this.deviceManagementClient.getSetDeviceLifecycleStatusResponse(asyncRequest);
            } catch (final Exception e) {
                // do nothing
            }
            assertNotNull("No response found for Set Device Lifecycle Status", response);
            assertNotEquals(OsgpResultType.NOT_FOUND, response.getResult());
            assertEquals("Set Device Lifecycle Status result should be OK", OsgpResultType.OK, response.getResult());
        });

        final String deviceLifecycleStatus = settings.get(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS);
        final Device device = this.DeviceRepository
                .findByDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

        assertEquals("Device Lifecycle Status should be " + deviceLifecycleStatus,
                com.alliander.osgp.domain.core.valueobjects.DeviceLifecycleStatus.valueOf(deviceLifecycleStatus),
                device.getDeviceLifecycleStatus());
    }

    @Then("^the status change is logged in the audit trail$")
    public void theStatusChangeIsLoggedInTheAuditTrail(final Map<String, String> settings) throws Throwable {
        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

        final List<DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, new PageRequest(0, 2)).getContent();

        for (final DeviceLogItem deviceLogItem : deviceLogItems) {
            LOGGER.info("CreationTime: {}", deviceLogItem.getCreationTime().toString());
            LOGGER.info("DecodedMessage: {}", deviceLogItem.getDecodedMessage());

            final boolean isMatchRetryOperation = Pattern.matches(PATTERN_RETRY_OPERATION,
                    deviceLogItem.getDecodedMessage());
            assertTrue("No retry log item found.", isMatchRetryOperation);
        }
    }
}
