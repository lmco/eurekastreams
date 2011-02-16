/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DomainGroupToDomainGroupModelViewTransfomer.
 * 
 */
public class DomainGroupToDomainGroupModelViewTransfomerTest
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
     * mocked tab for testing results.
     */
    private DomainGroup originalObject = context.mock(DomainGroup.class);

    /**
     * System under test.
     */
    private DomainGroupToDomainGroupModelViewTransfomer sut = new DomainGroupToDomainGroupModelViewTransfomer();

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(originalObject).getShortName();
                will(returnValue("gsn"));

                oneOf(originalObject).getParentOrganizationShortName();
                will(returnValue("posn"));

                oneOf(originalObject).isPending();
                will(returnValue(false));
            }
        });

        DomainGroupModelView dgmv = sut.transform(originalObject);

        assertEquals("gsn", dgmv.getShortName());
        assertEquals("posn", dgmv.getParentOrganizationShortName());
        assertEquals(false, dgmv.isPending());

        context.assertIsSatisfied();
    }
}
