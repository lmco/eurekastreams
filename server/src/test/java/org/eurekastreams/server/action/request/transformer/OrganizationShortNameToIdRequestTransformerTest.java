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
package org.eurekastreams.server.action.request.transformer;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for OrganizationShortNameToIdRequestTransformer class.
 *
 */
public class OrganizationShortNameToIdRequestTransformerTest
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
     * Mapper to get organizations from short names.
     */
    private GetOrganizationsByShortNames getOrganizationsByShortNamesMapper = context
            .mock(GetOrganizationsByShortNames.class);

    /**
     * Key for tests.
     */
    private String key = "key";

    /**
     * Org shortname for tests.
     */
    private String orgShortName = "orgShortName";

    /**
     * {@link OrganizationShortNameToIdRequestTransformer}.
     */
    private OrganizationShortNameToIdRequestTransformer sut = new OrganizationShortNameToIdRequestTransformer(
            getOrganizationsByShortNamesMapper, key);

    /**
     * Test.
     */
    @Test
    public void testTransformLongResult()
    {
        HashMap<String, Serializable> hash = new HashMap<String, Serializable>();
        hash.put(key, orgShortName);

        ServiceActionContext currentContext = new ServiceActionContext(hash, null);

        context.checking(new Expectations()
        {
            {
                allowing(getOrganizationsByShortNamesMapper).fetchId(orgShortName);
                will(returnValue(5L));
            }
        });

        assertEquals(5L, sut.transform(currentContext));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testTransformStringResult()
    {
        HashMap<String, Serializable> hash = new HashMap<String, Serializable>();
        hash.put(key, orgShortName);

        ServiceActionContext currentContext = new ServiceActionContext(hash, null);
        sut.setReturnValueAsString(true);

        context.checking(new Expectations()
        {
            {
                allowing(getOrganizationsByShortNamesMapper).fetchId(orgShortName);
                will(returnValue(5L));
            }
        });

        assertEquals("5", sut.transform(currentContext));
        context.assertIsSatisfied();
    }
}
