/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test update Bio execution.
 * 
 */
public class UpdateBiographyExecutionTest extends MapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * pMapper to use to update bio.
     */
    @Autowired
    PersonMapper pMapper;

    /**
     * subject under test.
     */
    UpdateBiographyExecution sut;

    /**
     * Principal mock.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * test that it works.
     */
    @Test
    public void testexecution()
    {

        final String biotext = "this is my biography it is cool.";
        final String testId = "fordp";

        HashMap<String, Serializable> formdata = new HashMap<String, Serializable>();
        formdata.put("biography", biotext);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(testId));
            }
        });

        final ServiceActionContext currentContext = new ServiceActionContext(formdata, principalMock);

        sut = new UpdateBiographyExecution(pMapper);

        String output = (String) sut.execute(currentContext);
        context.assertIsSatisfied();
        // test that output is correct
        assertEquals(biotext, output);
        // test that output is same as DB.
        assertEquals(output, pMapper.findByAccountId(testId).getBiography());
    }

}
