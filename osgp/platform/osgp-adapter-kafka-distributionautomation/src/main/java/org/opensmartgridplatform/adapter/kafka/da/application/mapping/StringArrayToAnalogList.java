/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.Arrays;
import java.util.List;

import com.alliander.data.scadameasurementpublishedevent.AccumulationKind;
import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.AnalogValue;
import com.alliander.data.scadameasurementpublishedevent.MeasuringPeriodKind;
import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;

public interface StringArrayToAnalogList {

    List<Analog> convertToAnalogList(String[] values);

    default Analog createAnalog(final String description, final Float value, final UnitSymbol unitSymbol,
            final UnitMultiplier unitMultiplier) {
        return new Analog(Arrays.asList(new AnalogValue(null, value)), AccumulationKind.none, description,
                MeasuringPeriodKind.none, unitMultiplier, unitSymbol);
    }

}
