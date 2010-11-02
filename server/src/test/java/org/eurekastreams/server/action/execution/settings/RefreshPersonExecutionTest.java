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
package org.eurekastreams.server.action.execution.settings;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.cache.RemovePersonFromCacheMapper;
import org.eurekastreams.server.persistence.mappers.db.UpdatePersonMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdatePersonResponse;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RefreshPersonExecution.
 *
 */
public class RefreshPersonExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link UpdatePersonMapper}.
     */
    private UpdatePersonMapper updatePersonMapper = context.mock(UpdatePersonMapper.class);

    /**
     * {@link RemovePersonFromCacheMapper}.
     */
    private RemovePersonFromCacheMapper removePersonFromCacheMapper = context.mock(RemovePersonFromCacheMapper.class);

    /**
     * Person.
     */
    private Person ldapPerson = context.mock(Person.class, "person1");

    /**
     * Person.
     */
    private Person adPerson = context.mock(Person.class, "person2");

    /**
     * {@link TaskHandlerActionContext}.
     */
	private TaskHandlerActionContext taskHandlerActionContext = context.mock(TaskHandlerActionContext.class);

    /**
	* {@link ActionContext}.
	*/
	private ActionContext actionContext = context.mock(ActionContext.class);
	
	/**
     * System under test.
     */
    private RefreshPersonExecution sut = new RefreshPersonExecution(updatePersonMapper, removePersonFromCacheMapper);

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(ldapPerson));
                
                oneOf(updatePersonMapper).execute(with(ldapPerson));
                will(returnValue(new UpdatePersonResponse(adPerson, true)));
                
                oneOf(removePersonFromCacheMapper).execute(with(any(Person.class)));
            }
        });

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test where no cache update is needed.
     */
    @Test
    public void testExecuteNoUpdate()
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(ldapPerson));
                
                oneOf(updatePersonMapper).execute(with(ldapPerson));
                will(returnValue(new UpdatePersonResponse(adPerson, false)));
            }
        });

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }
}
