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
package org.eurekastreams.server.action.validation.start;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.validation.ValidationTestHelper;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for AddTabValidation class.
 * 
 */
@SuppressWarnings("unchecked")
public class AddTabValidationTest
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
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<Person> pMapper = context.mock(FindByIdMapper.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Person}.
     */
    private Person personMock = context.mock(Person.class);

    /**
     * List of tabs.
     */
    private List<Tab> tabListMock = context.mock(List.class);

    /**
     * System under test.
     */
    private AddTabValidation sut = new AddTabValidation(pMapper);

    /**
     * {@link Principal}.
     */
    private Principal principalMock = context.mock(Principal.class);
    
    /**
     * {@link FindByIdRequest}.
     */
    private FindByIdRequest personRequest = new FindByIdRequest("Person", 2L); 

    /**
     * Test.
     */
    @Test
    public void testValidateSuccess()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalMock).getId();
                will(returnValue(2L));

                oneOf(pMapper).execute(with(equalInternally(personRequest)));
                will(returnValue(personMock));

                oneOf(personMock).getTabs(TabGroupType.START);
                will(returnValue(tabListMock));

                oneOf(tabListMock).size();
                will(returnValue(Person.TAB_LIMIT - 1));

                oneOf(actionContext).getParams();
                will(returnValue(ValidationTestHelper.generateString(TabTemplate.MAX_TAB_NAME_LENGTH)));

            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();

    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testValidateTabNameToLong()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalMock).getId();
                will(returnValue(2L));

                oneOf(pMapper).execute(with(equalInternally(personRequest)));
                will(returnValue(personMock));

                oneOf(personMock).getTabs(TabGroupType.START);
                will(returnValue(tabListMock));

                oneOf(tabListMock).size();
                will(returnValue(Person.TAB_LIMIT - 1));

                oneOf(actionContext).getParams();
                will(returnValue(ValidationTestHelper.generateString(TabTemplate.MAX_TAB_NAME_LENGTH + 1)));

            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(ve.getMessage(), TabTemplate.MAX_TAB_NAME_MESSAGE);
            throw ve;
        }
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testValidateToManyTabs()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalMock).getId();
                will(returnValue(2L));

                oneOf(pMapper).execute(with(equalInternally(personRequest)));
                will(returnValue(personMock));

                oneOf(personMock).getTabs(TabGroupType.START);
                will(returnValue(tabListMock));

                oneOf(tabListMock).size();
                will(returnValue(Person.TAB_LIMIT));
            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(ve.getMessage(), Person.TAB_LIMIT_MESSAGE);
            throw ve;
        }
    }

}
