/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Action;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendarData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGasRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministrativeStatusData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetConfigurationRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetFirmwareVersionRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGasRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetAlarmNotificationsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetEncryptionKeyExchangeOnGMeterRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetPushSetupAlarmRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetPushSetupSmsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "wsSmartMeteringActionMapperService")
@Validated
public class ActionMapperService {

    @Autowired
    private ManagementMapper managementMapper;

    @Autowired
    private AdhocMapper adhocMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private InstallationMapper installationMapper;

    @Autowired
    private MonitoringMapper monitoringMapper;

    private static Map<Class<?>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();

    /**
     * Specifies which mapper to use for the ws class received.
     */
    @PostConstruct
    private void postConstruct() {
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterData.class,
                this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,
                this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData.class,
                this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData.class,
                this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequestData.class,
                this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData.class,
                this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData.class,
                this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusTypeData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarDataType.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP
                .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequestData.class,
                        this.configurationMapper);
        CLASS_TO_MAPPER_MAP
                .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData.class,
                        this.configurationMapper);
        CLASS_TO_MAPPER_MAP
        .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequestData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequestData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData.class,
                this.adhocMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetConfigurationRequestData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequestData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class,
                this.configurationMapper);

    }

    /**
     * Specifies to which core object the ws object needs to be mapped.
     */
    private static Map<Class<?>, Class<? extends ActionValueObject>> CLASS_MAP = new HashMap<>();
    static {
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData.class,
                SpecialDaysRequestData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterData.class,
                ReadAlarmRegisterData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,
                FindEventsQuery.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusData.class,
                GetAdministrativeStatusData.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData.class,
                PeriodicMeterReadsRequestData.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequestData.class,
                PeriodicMeterReadsGasRequestData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData.class,
                ActualMeterReadsRequestData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData.class,
                ActualMeterReadsGasRequestData.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusTypeData.class,
                AdministrativeStatusTypeData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarDataType.class,
                ActivityCalendarData.class);
        CLASS_MAP
                .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequestData.class,
                        SetEncryptionKeyExchangeOnGMeterRequestData.class);
        CLASS_MAP
                .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData.class,
                        SetAlarmNotificationsRequestData.class);
        CLASS_MAP
                .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData.class,
                        SetConfigurationObjectRequestData.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequestData.class,
                SetPushSetupAlarmRequestData.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequestData.class,
                SetPushSetupSmsRequestData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData.class,
                SynchronizeTimeRequestData.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetConfigurationRequestData.class,
                GetConfigurationRequestData.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequestData.class,
                GetFirmwareVersionRequestData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class, KeySet.class);

    }

    public List<ActionValueObject> mapAllActions(final List<? extends Action> actionList) throws FunctionalException {
        final List<ActionValueObject> actionValueObjectList = new ArrayList<>();

        for (final Action action : actionList) {

            final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());
            final Class<? extends ActionValueObject> clazz = CLASS_MAP.get(action.getClass());
            if (mapper != null) {
                actionValueObjectList.add(this.getActionValueObjectWithDefaultMapper(action, mapper, clazz));
            } else {
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING, new AssertionError("No mapper defined for class: "
                                + action.getClass().getName()));

            }
        }

        return actionValueObjectList;
    }

    private ActionValueObject getActionValueObjectWithDefaultMapper(final Action action,
            final ConfigurableMapper mapper, final Class<? extends ActionValueObject> clazz) throws FunctionalException {
        final ActionValueObject actionValueObject = mapper.map(action, clazz);

        if (actionValueObject == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No Value Object for Action of class: "
                            + action.getClass().getName()));
        }
        return actionValueObject;
    }

}
