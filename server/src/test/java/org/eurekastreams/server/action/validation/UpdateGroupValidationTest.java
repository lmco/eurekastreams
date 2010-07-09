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
package org.eurekastreams.server.action.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * 
 * Test for UpdateGroupValidation class.
 * 
 */
public class UpdateGroupValidationTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link ActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@Link GetOrganizationsByShortNames}.
     */
    private GetOrganizationsByShortNames orgMapperMock = context.mock(GetOrganizationsByShortNames.class);
    
    /**
     * {@link UpdateGroupValidation} system under test.
     */
    private UpdateGroupValidation sut = new UpdateGroupValidation(orgMapperMock);

    /**
     * Test validateParams() with valid inputs.
     */
    @Test
    public void validateParams()
    {

        HashSet<Person> coordinators = new HashSet<Person>();

        Person fakePerson = context.mock(Person.class);
        coordinators.add(fakePerson);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(DomainGroupModelView.ID_KEY, 2L);
        formData.put(DomainGroupModelView.URL_KEY, "http://www.google.com");
        formData.put(DomainGroupModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_DESCRIPTION_LENGTH));
        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper.generateString(DomainGroup.MAX_NAME_LENGTH));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_SHORT_NAME_LENGTH));
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, "isgs");
        formData.put(DomainGroupModelView.OVERVIEW_KEY, "this is unlimited length");
        formData.put(DomainGroupModelView.STREAM_COMMENTABLE_KEY, true);
        formData.put(DomainGroupModelView.STREAM_POSTABLE_KEY, true);
        formData.put(DomainGroupModelView.KEYWORDS_KEY, ValidationTestHelper
                .generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH)
                + "," + ValidationTestHelper.generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH));

        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
                oneOf(orgMapperMock).fetchUniqueResult("isgs");
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with invalid inputs attempt to update privacy settings.
     */
    @Test(expected = ValidationException.class)
    public void inValidateParamsHackingAttempt()
    {

        HashSet<Person> coordinators = new HashSet<Person>();

        Person fakePerson = context.mock(Person.class);
        coordinators.add(fakePerson);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(DomainGroupModelView.ID_KEY, 2L);
        formData.put(DomainGroupModelView.URL_KEY, "http://www.google.com");
        formData.put(DomainGroupModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_DESCRIPTION_LENGTH));
        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper.generateString(DomainGroup.MAX_NAME_LENGTH));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_SHORT_NAME_LENGTH));
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, "isgs");
        formData.put(DomainGroupModelView.OVERVIEW_KEY, "this is unlimited length");
        formData.put(DomainGroupModelView.STREAM_COMMENTABLE_KEY, true);
        formData.put(DomainGroupModelView.STREAM_POSTABLE_KEY, true);
        formData.put(DomainGroupModelView.PRIVACY_KEY, true);
        formData.put(DomainGroupModelView.KEYWORDS_KEY, ValidationTestHelper
                .generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH)
                + "," + ValidationTestHelper.generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH));

        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
                allowing(actionContext).getPrincipal().getAccountId();
                oneOf(orgMapperMock).fetchUniqueResult("isgs");

            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with bad items that don't meet requirements.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithInvalidParams()
    {
        HashSet<Person> coordinators = new HashSet<Person>();

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(DomainGroupModelView.ID_KEY, 2L);
        formData.put(DomainGroupModelView.URL_KEY, "www.google.com");
        formData.put(DomainGroupModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_DESCRIPTION_LENGTH + 1));
        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_SHORT_NAME_LENGTH));
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, null);
        formData.put(DomainGroupModelView.KEYWORDS_KEY, ValidationTestHelper
                .generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH)
                + "," + ValidationTestHelper.generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);
        formData.put(DomainGroupModelView.OVERVIEW_KEY, "this is unlimited length");
        formData.put(DomainGroupModelView.STREAM_COMMENTABLE_KEY, true);
        formData.put(DomainGroupModelView.STREAM_POSTABLE_KEY, true);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(6, ve.getErrors().size());
            assertTrue(ve.getErrors().containsValue(DomainGroup.WEBSITE_MESSAGE));
            assertTrue(ve.getErrors().containsValue(UpdateGroupValidation.MUST_HAVE_PARENT_ORG_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroup.MIN_COORDINATORS_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroupModelView.KEYWORD_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroup.DESCRIPTION_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroup.NAME_LENGTH_MESSAGE));
            throw ve;
        }
        context.assertIsSatisfied();
    }
    
    /**
     * Test validateParams() with bad items this one spesifically for a org that no longer exist.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithInvalidOrgThatDoesntExist()
    {
        HashSet<Person> coordinators = new HashSet<Person>();

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        formData.put(DomainGroupModelView.ID_KEY, 2L);
        formData.put(DomainGroupModelView.URL_KEY, "www.google.com");
        formData.put(DomainGroupModelView.DESCRIPTION_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_DESCRIPTION_LENGTH + 1));
        formData.put(DomainGroupModelView.NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.SHORT_NAME_KEY, ValidationTestHelper
                .generateString(DomainGroup.MAX_SHORT_NAME_LENGTH));
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, "noSuchParentOrg");
        formData.put(DomainGroupModelView.KEYWORDS_KEY, ValidationTestHelper
                .generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH)
                + "," + ValidationTestHelper.generateString(BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH + 1));
        formData.put(DomainGroupModelView.COORDINATORS_KEY, coordinators);
        formData.put(DomainGroupModelView.OVERVIEW_KEY, "this is unlimited length");
        formData.put(DomainGroupModelView.STREAM_COMMENTABLE_KEY, true);
        formData.put(DomainGroupModelView.STREAM_POSTABLE_KEY, true);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
                oneOf(orgMapperMock).fetchUniqueResult("noSuchParentOrg");
                will(returnValue(null));
                
            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(6, ve.getErrors().size());
            assertTrue(ve.getErrors().containsValue(DomainGroup.WEBSITE_MESSAGE));
            assertTrue(ve.getErrors().containsValue(UpdateGroupValidation.NO_SUCH_PARENT_ORG));
            assertTrue(ve.getErrors().containsValue(DomainGroup.MIN_COORDINATORS_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroupModelView.KEYWORD_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroup.DESCRIPTION_LENGTH_MESSAGE));
            assertTrue(ve.getErrors().containsValue(DomainGroup.NAME_LENGTH_MESSAGE));
            throw ve;
        }
        context.assertIsSatisfied();
    }
    
    /**
     * Test validateParams() no required field provided.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsNoRequiredFields()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertEquals(7, ve.getErrors().size());
            throw ve;
        }
        context.assertIsSatisfied();
    }
}
