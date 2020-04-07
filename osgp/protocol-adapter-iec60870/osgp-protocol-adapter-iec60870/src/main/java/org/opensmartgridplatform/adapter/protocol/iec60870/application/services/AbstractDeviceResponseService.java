/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceMap;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDeviceResponseService implements DeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeviceResponseService.class);

    @Autowired
    private DeviceResponseServiceMap deviceResponseServiceMap;

    private final DeviceType deviceType;

    public AbstractDeviceResponseService(final DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    @PostConstruct
    private void registerService() {
        LOGGER.info("Registering device response service for device type {}", this.deviceType);
        this.deviceResponseServiceMap.register(this.deviceType, this);
    }
}
