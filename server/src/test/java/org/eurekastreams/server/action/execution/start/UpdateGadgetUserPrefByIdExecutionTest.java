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
package org.eurekastreams.server.action.execution.start;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.start.GadgetUserPrefActionRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for UpdateGadgetUserPrefByIdExecution class.
 * 
 */
@SuppressWarnings("unchecked")
public class UpdateGadgetUserPrefByIdExecutionTest
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
     * Test instance of the FindByIdMapper for gadgets.
     */
    private FindByIdMapper<Gadget> findGadgetByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * Test instance of the UpdateMapper for gadgets.
     */
    private UpdateMapper<Gadget> updateMapper = context.mock(UpdateMapper.class);

    /**
     * Test instance of the Gadget.
     */
    private Gadget testGadget = context.mock(Gadget.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Test instance of the UpdateGadgetUserPrefByIdAction for the system under test.
     */
    private UpdateGadgetUserPrefByIdExecution sut;

    /**
     * This method preps the tests in this class.
     */
    @Before
    public void setup()
    {
        sut = new UpdateGadgetUserPrefByIdExecution(updateMapper, findGadgetByIdMapper);
    }

    /**
     * Testing perform action method.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testExecute() throws Exception
    {
        final String userPrefsJson = "{'userPref1':'value1','userPref2':'value2'}";
        final GadgetUserPrefActionRequest requestParam = new GadgetUserPrefActionRequest(new Long(1L), userPrefsJson);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(requestParam));

                oneOf(findGadgetByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(testGadget));

                oneOf(testGadget).setGadgetUserPref(with(any(String.class)));

                oneOf(updateMapper).execute(with(any(PersistenceRequest.class)));

                oneOf(testGadget).getGadgetUserPref();
                will(returnValue(userPrefsJson));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
