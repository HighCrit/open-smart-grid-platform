/**
 * Copyright 2020 Alliander N.V.
 * Copyright 2012-20 Fraunhofer ISE
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file was originally part of jDLMS, where it was part of a group of classes residing in packages
 * org.openmuc.jdlms.interfaceclass, org.openmuc.jdlms.interfaceclass.attribute and org.openmuc.jdlms.interfaceclass.method
 * that have been deprecated for jDLMS since version 1.5.1.
 *
 * It has been copied to the GXF code base under the Apache License, Version 2.0 with the permission of Fraunhofer ISE.
 * For more information about jDLMS visit
 *
 * http://www.openmuc.org
 */
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/**
 * This class contains the attributes defined for IC TcpUdpSetup.
 */
public enum TcpUdpSetupAttribute implements AttributeClass {

    LOGICAL_NAME(1),
    TCP_UDP_PORT(2),
    IP_REFERENCE(3),
    MSS(4),
    NB_OF_SIM_CONN(5),
    INACTIVITY_TIME_OUT(6);

    static final InterfaceClass INTERFACE_CLASS = InterfaceClass.TCP_UDP_SETUP;

    private final int attributeId;

    private TcpUdpSetupAttribute(final int attributeId) {
        this.attributeId = attributeId;
    }

    @Override
    public int attributeId() {
        return this.attributeId;
    }

    @Override
    public String attributeName() {
        return this.name();
    }

    @Override
    public InterfaceClass interfaceClass() {
        return INTERFACE_CLASS;
    }
}
