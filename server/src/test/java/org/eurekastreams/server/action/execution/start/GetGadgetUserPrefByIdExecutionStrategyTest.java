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

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.start.GadgetUserPrefActionRequest;
import org.eurekastreams.server.domain.GadgetUserPrefDTO;
import org.eurekastreams.server.persistence.mappers.opensocial.GetGadgetUserPrefMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GadgetUserPrefRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the GetGadgetUserPrefByIdExecutionStrategy.
 */
public class GetGadgetUserPrefByIdExecutionStrategyTest
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
     * Test instance of the gadget user pref mapper.
     */
    private GetGadgetUserPrefMapper mapper = context.mock(GetGadgetUserPrefMapper.class);

    /**
     * GetGadgetUserPrefByIdExecutionStrategy to be tested.
     */
    private GetGadgetUserPrefByIdExecutionStrategy sut;

    /**
     * Setup for the tests in this class.
     */
    @Before
    public void setup()
    {
        sut = new GetGadgetUserPrefByIdExecutionStrategy(mapper);
    }

    /**
     * Test a successful run of the action.
     *
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testPerformAction() throws Exception
    {
        final String userPrefsJson = "{'userPref1':'value1','userPref2':'value2'}";
        final GadgetUserPrefDTO testPrefs1 = context.mock(GadgetUserPrefDTO.class);
        final GadgetUserPrefActionRequest requestParam = new GadgetUserPrefActionRequest(new Long(1L), userPrefsJson);
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(any(GadgetUserPrefRequest.class)));
                will(returnValue(testPrefs1));

                oneOf(testPrefs1).getJsonUserPref();
                will(returnValue(userPrefsJson));
            }
        });

        sut.execute(new ActionContext()
        {
            private static final long serialVersionUID = 1846377112179005545L;

            @Override
            public Serializable getParams()
            {
                return requestParam;
            }

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

        });
        context.assertIsSatisfied();
    }
}
