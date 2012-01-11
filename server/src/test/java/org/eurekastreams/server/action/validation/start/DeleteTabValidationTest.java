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
package org.eurekastreams.server.action.validation.start;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;
import org.eurekastreams.server.search.modelview.TabDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteTabValidation class.
 */
public class DeleteTabValidationTest
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
     * PersonPagePropertiesByIdMapper.
     */
    @SuppressWarnings("unchecked")
    private DomainMapper<Long, PersonPagePropertiesDTO> personPagePropertiesByIdMapper = context
            .mock(DomainMapper.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Principal id.
     */
    private Long principalId = 2L;

    /**
     * PersonPagePropertiesDTO mock.
     */
    private PersonPagePropertiesDTO pppDto = context.mock(PersonPagePropertiesDTO.class);

    /**
     * TabDTO.
     */
    private TabDTO tabDTO1 = context.mock(TabDTO.class, "tabDTO1");

    /**
     * TabDTO.
     */
    private TabDTO tabDTO2 = context.mock(TabDTO.class, "tabDTO2");

    /**
     * System under test.
     */
    private DeleteTabValidation sut = new DeleteTabValidation(personPagePropertiesByIdMapper);

    /**
     * Test.
     */
    @Test
    public void testValidateSuccess()
    {
        final List<TabDTO> tabs = new ArrayList<TabDTO>();
        tabs.add(tabDTO1);
        tabs.add(tabDTO2);
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalMock).getId();
                will(returnValue(principalId));

                oneOf(personPagePropertiesByIdMapper).execute(principalId);
                will(returnValue(pppDto));

                oneOf(pppDto).getTabDTOs();
                will(returnValue(tabs));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testValidateFail()
    {
        final List<TabDTO> tabs = new ArrayList<TabDTO>();
        tabs.add(tabDTO1);
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalMock).getId();
                will(returnValue(principalId));

                oneOf(personPagePropertiesByIdMapper).execute(principalId);
                will(returnValue(pppDto));

                oneOf(pppDto).getTabDTOs();
                will(returnValue(tabs));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

}
