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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.action.request.opensocial.GetAppDataRequest;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.domain.dto.AppDataDTO;
import org.eurekastreams.server.persistence.AppDataMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for GetAppDataDTODbMapper.
 */
public class GetAppDataDTODbMapperTest
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
     * AppData entity mapper.
     */
    private AppDataMapper entityMapper = context.mock(AppDataMapper.class);

    /**
     * {@link AppData}.
     */
    private AppData appData = context.mock(AppData.class);

    /**
     * System under test.
     */
    private GetAppDataDTODbMapper sut = new GetAppDataDTODbMapper(entityMapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final Map<String, String> values = new HashMap<String, String>();
        context.checking(new Expectations()
        {
            {
                oneOf(entityMapper).findOrCreateByPersonAndGadgetDefinitionIds(5L, "9");
                will(returnValue(appData));

                oneOf(appData).getValues();
                will(returnValue(values));
            }
        });

        AppDataDTO result = sut.execute(new GetAppDataRequest(5L, "9"));

        assertEquals(5L, result.getGadgetDefinitionId());
        assertEquals("9", result.getOpenSocialId());
        assertEquals(values, result.getKeyValuePairs());

        context.assertIsSatisfied();
    }
}
