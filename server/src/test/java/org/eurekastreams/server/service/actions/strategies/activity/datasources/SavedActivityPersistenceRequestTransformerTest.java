/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Saved activity transformer test.
 */
public class SavedActivityPersistenceRequestTransformerTest
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
     * System under test.
     */
    private SavedActivityPersistenceRequestTransformer sut;

    /**
     * Person mapper mock.
     */
    private GetPeopleByAccountIds personMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * Setup test fixtures.
     */
    @Before
    public void setUP()
    {
        sut = new SavedActivityPersistenceRequestTransformer(personMapper);
    }

    /**
     * Tests the transformation.
     */
    @Test
    public void transformTestMatchingId()
    {
        final Long entityId = 10L;
        final String entityAcctName = "acctName";
        
        final JSONObject jsonReq = new JSONObject();
        jsonReq.accumulate("savedBy", entityAcctName);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).fetchId(entityAcctName);
                will(returnValue(entityId));
            }
        });

        Assert.assertEquals(entityId, sut.transform(jsonReq, entityId));
    }
    
    /**
     * Tests the transformation.
     */
    @Test(expected=AuthorizationException.class)
    public void transformTestNotMatchingId()
    {
        final Long entityId = 10L;
        final String entityAcctName = "acctName";
        
        final JSONObject jsonReq = new JSONObject();
        jsonReq.accumulate("savedBy", entityAcctName);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).fetchId(entityAcctName);
                will(returnValue(entityId - 1L));
            }
        });

        Assert.assertEquals(entityId, sut.transform(jsonReq, entityId));
    }

}
