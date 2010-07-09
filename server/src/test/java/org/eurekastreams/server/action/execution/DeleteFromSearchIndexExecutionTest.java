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
package org.eurekastreams.server.action.execution;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.DeleteFromSearchIndexRequest;
import org.eurekastreams.server.persistence.mappers.DeleteFromSearchIndex;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteFromSearchIndexExecution class.
 * 
 */
public class DeleteFromSearchIndexExecutionTest
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
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Delete from search index mapper.
     */
    DeleteFromSearchIndex deleteFromSearchIndexDAO = context.mock(DeleteFromSearchIndex.class);

    /**
     * System under test.
     */
    private DeleteFromSearchIndexExecution sut;

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        sut = new DeleteFromSearchIndexExecution(deleteFromSearchIndexDAO);
        final DeleteFromSearchIndexRequest params = new DeleteFromSearchIndexRequest(String.class,
                new ArrayList<Long>());

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(params));

                allowing(deleteFromSearchIndexDAO).execute(params);
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

}
