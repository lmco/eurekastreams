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

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ActivityIdToRecipientParentOrgIdRequestTransformer.
 */
public class ActivityIdToRecipientParentOrgIdRequestTransformerTest
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
     * Mapper to get activities.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>>  activitiesMapper = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private ActivityIdToRecipientParentOrgIdRequestTransformer sut
    // line break
    = new ActivityIdToRecipientParentOrgIdRequestTransformer(activitiesMapper);

    /**
     * Test transforming activity id to recipient organization id.
     */
    @Test
    public void testTransform()
    {
        final Long activityId = 3872L;
        final Long recipientOrgId = 882781L;

        final ActivityDTO activity = new ActivityDTO();
        activity.setRecipientParentOrgId(recipientOrgId);

        final ActionContext actionContext = context.mock(ActionContext.class);

        context.checking(new Expectations()
        {
            {
                oneOf(activitiesMapper).execute(Arrays.asList(activityId));
                will(returnValue(Arrays.asList(activity)));

                oneOf(actionContext).getParams();
                will(returnValue(activityId));
            }
        });

        assertEquals(recipientOrgId.toString(), sut.transform(actionContext));
    }
}
