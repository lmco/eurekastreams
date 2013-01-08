/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain.strategies;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Test fixture for PersonModelViewAvatarDisplayTransformer.
 */
public class PersonModelViewAvatarDisplayTransformerTest
{
    /**
     * System under test.
     */
    private final PersonModelViewAvatarDisplayTransformer sut = new PersonModelViewAvatarDisplayTransformer();

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
     * Test transform.
     */
    @Test
    public void testTransform()
    {
        final PersonModelView mv = context.mock(PersonModelView.class);
        final String accountId = "lsdjkfdskfl";
        final String avatarId = "lsdkjflsdkflsdjf";
        final String displayName = "sldkjfskldjfljdf";
        final boolean accountLocked = true;
        final long entityId = 3872L;
        context.checking(new Expectations()
        {
            {
                oneOf(mv).getAccountId();
                will(returnValue(accountId));

                oneOf(mv).getAvatarId();
                will(returnValue(avatarId));

                oneOf(mv).getDisplayName();
                will(returnValue(displayName));

                oneOf(mv).getEntityId();
                will(returnValue(entityId));

                oneOf(mv).isAccountLocked();
                will(returnValue(accountLocked));
            }
        });

        ArrayList<PersonModelView> people;
        people = sut.transform(sut.transform(Collections.singletonList(mv)));

        assertEquals(accountId, people.get(0).getAccountId());
        assertEquals(avatarId, people.get(0).getAvatarId());
        assertEquals(displayName, people.get(0).getDisplayName());
        assertEquals(entityId, people.get(0).getEntityId());
        assertEquals(accountLocked, people.get(0).isAccountLocked());

        context.assertIsSatisfied();
    }
}
