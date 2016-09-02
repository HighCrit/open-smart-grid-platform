/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

/**
 * Super class for TestCase runner implementations that communicate with
 * SmartMetering web services. Each Runner will be called from a subclass.
 */
public abstract class SmartMetering extends SoapTestCase {

    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";

    protected String correlationUid;

    @Autowired
    private OrganisationId organisationId;

    @Override
    protected void requestRunner(final TestStepStatus testStepStatus, final Map<String, String> propertiesMap,
            final String testCaseNameRequest, final String testCaseXml, final String testSuiteXml) throws Throwable {

        super.requestRunner(testStepStatus, propertiesMap, testCaseNameRequest, testCaseXml, testSuiteXml);

        final Pattern correlationUidPattern = Pattern.compile(this.organisationId.getOrganisationId()
                + XPATH_MATCHER_CORRELATIONUID);
        final Matcher correlationUidMatcher = correlationUidPattern.matcher(this.response);
        if (testStepStatus == TestStepStatus.OK) {
            assertTrue(correlationUidMatcher.find());
            this.correlationUid = correlationUidMatcher.group();
        }
    }
}
