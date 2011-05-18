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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GroupRecipientRetriever class.
 * 
 */
public class GroupRecipientRetrieverTest
{

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private GroupRecipientRetriever sut;

    /**
     * mapper.
     */
    private DomainGroupMapper mapper = context.mock(DomainGroupMapper.class);

    /**
     * ActivityDTO.
     */
    private ActivityDTO activityDTOMock = context.mock(ActivityDTO.class);

    /**
     * StreamEntityDTO.
     */
    private StreamEntityDTO streamEntityDTOMock = context.mock(StreamEntityDTO.class);

    /**
     * Mocked domain group.
     */
    private DomainGroup domainGroupMock = context.mock(DomainGroup.class);

    /**
     * Pre-test setup.
     */
    @Before
    public void setup()
    {
        sut = new GroupRecipientRetriever(mapper);
    }

    /**
     * Test isDestinationStreamPublic() on true.
     */
    @Test
    public void testIsDestinationStreamPublicOnTrue()
    {
        testIsDestinationStreamPublic(true);
    }

    /**
     * Test isDestinationStreamPublic() on false.
     */
    @Test
    public void testIsDestinationStreamPublicOnFalse()
    {
        testIsDestinationStreamPublic(false);
    }

    /**
     * Helper method to test isDestinationStreamPublic for a canned response.
     * 
     * @param inIsIt
     *            whether or not the group is public
     */
    private void testIsDestinationStreamPublic(final boolean inIsIt)
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOMock).getDestinationStream();
                will(returnValue(streamEntityDTOMock));

                oneOf(streamEntityDTOMock).getUniqueIdentifier();
                will(returnValue("groupShortName"));

                oneOf(mapper).findByShortName("groupShortName");
                will(returnValue(domainGroupMock));

                oneOf(domainGroupMock).isPublicGroup();
                will(returnValue(inIsIt));
            }
        });

        assertEquals(inIsIt, sut.isDestinationStreamPublic(activityDTOMock));
        context.assertIsSatisfied();
    }
}
