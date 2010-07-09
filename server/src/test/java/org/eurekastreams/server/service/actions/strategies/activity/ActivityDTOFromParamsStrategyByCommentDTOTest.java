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
package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ActivityDTOFromParamsStrategyByCommentDTO.
 * 
 */
public class ActivityDTOFromParamsStrategyByCommentDTOTest
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
     * ActivityDTO DAO.
     */
    private BulkActivitiesMapper activitiesDAO = context.mock(BulkActivitiesMapper.class);

    /**
     * UserDeatils mock.
     */
    private Principal user = context.mock(Principal.class);

    /**
     * CommentDTO mock.
     */
    private CommentDTO commentDTO = context.mock(CommentDTO.class);

    /**
     * Test the execute method.
     */
    @Test
    public void testExecute()
    {
        ActivityDTOFromParamsStrategyByCommentDTO sut = new ActivityDTOFromParamsStrategyByCommentDTO(activitiesDAO);
        final List<Long> activityIds = new ArrayList<Long>(1);
        activityIds.add(5L);
        final List<ActivityDTO> dtos = new ArrayList<ActivityDTO>(1);
        dtos.add(context.mock(ActivityDTO.class));

        context.checking(new Expectations()
        {
            {
                oneOf(commentDTO).getActivityId();
                will(returnValue(5L));

                oneOf(user).getAccountId();
                will(returnValue("userName"));

                oneOf(activitiesDAO).execute(activityIds, "userName");
                will(returnValue(dtos));
            }
        });

        sut.execute(user, commentDTO);
        context.assertIsSatisfied();
    }
}
