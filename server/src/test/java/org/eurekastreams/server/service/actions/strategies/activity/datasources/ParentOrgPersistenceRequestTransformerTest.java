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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests transforming a parent org request.
 */
public class ParentOrgPersistenceRequestTransformerTest
{
    /**
     * Mocking context.
     */
    private static final JUnit4Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static ParentOrgPersistenceRequestTransformer sut;

    /**
     * Bulk people mapper.
     */
    private static GetPeopleByAccountIds bulkPeopleMapper = CONTEXT.mock(GetPeopleByAccountIds.class);

    /**
     * Setup test fixtures.
     */
    @BeforeClass
    public static void setup()
    {
        sut = new ParentOrgPersistenceRequestTransformer(bulkPeopleMapper);
    }

    /**
     * Tests executing.
     */
    @Test
    public void testTransform()
    {
        final String personName = "personName";
        final String orgShortName = "shortName";

        final JSONObject request = new JSONObject();
        request.accumulate("parentOrg", personName);

        final PersonModelView person = CONTEXT.mock(PersonModelView.class);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(bulkPeopleMapper).fetchUniqueResult(personName);
                will(returnValue(person));

                oneOf(person).getParentOrganizationShortName();
                will(returnValue(orgShortName));
            }
        });

        String result = (String) sut.transform(request, 1L);

        Assert.assertEquals(orgShortName, result);

        CONTEXT.assertIsSatisfied();
    }
}
