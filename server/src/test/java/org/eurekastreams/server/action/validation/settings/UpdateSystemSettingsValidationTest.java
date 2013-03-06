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
package org.eurekastreams.server.action.validation.settings;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for UpdateSystemSettingsValidation class.
 * 
 */
public class UpdateSystemSettingsValidationTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to get people by ids.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> peopleByIdsMapper = context.mock(DomainMapper.class,
            "peopleByIdsMapper");

    /**
     * Admins set.
     */
    private HashSet<Person> admins;

    /**
     * Mocked admin person.
     */
    private final Person admin = context.mock(Person.class, "admin1");

    /**
     * Modelview for the admin.
     */
    private PersonModelView adminModelView;

    /**
     * Admin ids.
     */
    private ArrayList<Long> adminIds;

    /**
     * ID of the admin.
     */
    private final Long adminId = 2828L;

    /**
     * Admin account id.
     */
    private String adminAccountId = "adminaccountid";

    /**
     * List of person modelviews returned from peopleByIdsMapper.
     */
    private ArrayList<PersonModelView> adminModelViews;

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link UpdateSystemSettingsValidation} system under test.
     */
    private UpdateSystemSettingsValidation sut = new UpdateSystemSettingsValidation(peopleByIdsMapper);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        admins = new HashSet<Person>();
        admins.add(admin);

        adminIds = new ArrayList<Long>();
        adminIds.add(adminId);

        adminModelViews = new ArrayList<PersonModelView>();

        adminModelView = new PersonModelView();
        adminModelView.setEntityId(adminId);
        adminModelView.setAccountId(adminAccountId);
        adminModelViews.add(adminModelView);

        context.checking(new Expectations()
        {
            {
                allowing(admin).getId();
                will(returnValue(adminId));

                allowing(admin).getAccountId();
                will(returnValue(adminAccountId));
            }
        });
    }

    /**
     * Get a valid map of parameters.
     * 
     * @return a valid map of parameters
     */
    private HashMap<String, Serializable> getSuccessMap()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("contentExpiration", 1);
        formData.put("termsOfService", "tos");
        formData.put("siteLabel", "sl");
        formData.put("contentWarningText", "cw");
        formData.put("streamViewId", 9L);

        formData.put("admins", admins);

        return formData;
    }

    /**
     * Test validateParams() with valid inputs.
     */
    @Test
    public void validateParamsWithMap()
    {
        final HashMap<String, Serializable> formData = getSuccessMap();
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));

                oneOf(peopleByIdsMapper).execute(with(IsEqualInternally.equalInternally(adminIds)));
                will(returnValue(adminModelViews));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with locked admin.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithLockedAdmin()
    {
        final HashMap<String, Serializable> formData = getSuccessMap();
        context.checking(new Expectations()
        {
            {

                allowing(actionContext).getParams();
                will(returnValue(formData));

                oneOf(peopleByIdsMapper).execute(with(IsEqualInternally.equalInternally(adminIds)));
                will(returnValue(adminModelViews));
            }
        });

        adminModelView.setAccountLocked(true);

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertTrue(ve.getErrors().containsKey("admins"));
            Assert.assertEquals(UpdateSystemSettingsValidation.SYSTEM_ADMINISTRATOR_LOCKED_OUT_ERROR_MESSAGE
                    + adminAccountId, ve.getErrors().get("admins"));
            throw ve;
        }

        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with no admins listed.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithNoAdmins()
    {
        final HashMap<String, Serializable> formData = getSuccessMap();
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));

            }
        });

        admins.clear();

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertTrue(ve.getErrors().containsKey("admins"));
            Assert.assertEquals(UpdateSystemSettingsValidation.SYSTEM_ADMINISTRATORS_EMPTY_ERROR_MESSAGE, ve
                    .getErrors().get("admins"));
            throw ve;
        }

        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with no admins listed.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithAdminNotFound()
    {
        final HashMap<String, Serializable> formData = getSuccessMap();
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));

                oneOf(peopleByIdsMapper).execute(with(IsEqualInternally.equalInternally(adminIds)));
                will(returnValue(new ArrayList<PersonModelView>()));
            }
        });

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertTrue(ve.getErrors().containsKey("admins"));
            Assert.assertEquals(UpdateSystemSettingsValidation.SYSTEM_ADMINISTRATOR_NOTFOUND_ERROR_MESSAGE
                    + adminAccountId, ve.getErrors().get("admins"));
            throw ve;
        }

        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with admins not found.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithAdmins()
    {
        final HashMap<String, Serializable> formData = getSuccessMap();
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));

                oneOf(peopleByIdsMapper).execute(with(IsEqualInternally.equalInternally(adminIds)));
                will(returnValue(adminModelViews));
            }
        });

        adminModelViews.clear();

        try
        {
            sut.validate(actionContext);
        }
        catch (ValidationException ve)
        {
            context.assertIsSatisfied();
            assertTrue(ve.getErrors().containsKey("admins"));
            Assert.assertEquals(UpdateSystemSettingsValidation.SYSTEM_ADMINISTRATOR_NOTFOUND_ERROR_MESSAGE
                    + adminAccountId, ve.getErrors().get("admins"));
            throw ve;
        }

        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with valid inputs.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsNoParams()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(null));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with invalid data.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsBadData()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("termsOfService", null);
        formData.put("siteLabel", null);
        formData.put("contentWarningText", null);
        formData.put("contentExpiration", false);
        formData.put("tosPromptInterval", "notvalid");

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
            assertTrue(ve.getErrors().containsKey("termsOfService"));
            assertTrue(ve.getErrors().containsKey("siteLabel"));
            assertTrue(ve.getErrors().containsKey("contentWarningText"));
            assertTrue(ve.getErrors().containsKey("contentExpiration"));
            assertTrue(ve.getErrors().containsKey("tosPromptInterval"));
            throw ve;
        }
    }

    /**
     * Test validateParams() with more invalid data.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsMoreBadData()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("contentExpiration", -1);
        formData.put("tosPromptInterval", -1);

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
            assertTrue(ve.getErrors().containsKey("contentExpiration"));
            assertTrue(ve.getErrors().containsKey("tosPromptInterval"));
            throw ve;
        }
    }

    /**
     * Test validateParams() with data that is too large for the fields.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsTooLargeData()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();

        String over50Chars = "Lorem ipsum dolor sit amet, consectetur massa nunc. ";

        String over255Chars = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis quis sem a eros"
                + " consequat facilisis non quis justo. Etiam vel dolor lacus, id bibendum ante. Curabitur dapibus,"
                + " nunc a semper tristique, orci sapien feugiat nisi, sed pharetra metus ipsum metus. ";

        String over1000Chars = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempor"
                + " feugiat diam vel elementum. Donec magna velit, molestie et vehicula at, vehicula congue"
                + " risus. Curabitur pulvinar convallis metus, sed laoreet augue dapibus non. Nullam pretium"
                + " congue ante id adipiscing. Proin quis consectetur lacus. Sed ac nisl nec tellus pretium"
                + " accumsan. Sed orci massa, volutpat nec tincidunt in, condimentum non ligula. Ut varius"
                + " egestas ullamcorper. Mauris commodo, lorem sed volutpat semper, sem nunc dapibus diam, vitae"
                + " auctor felis massa ac neque. Etiam tempor arcu eu mi aliquet ac scelerisque eros sollicitudin."
                + " Praesent sed est volutpat tellus porttitor suscipit. Suspendisse et quam nec magna porttitor"
                + " vulputate vitae id magna. Suspendisse vestibulum ipsum eget leo molestie imperdiet.  Aenean"
                + " vel justo nisl. Donec felis justo, molestie eu dignissim faucibus, aliquam eu augue. Nullam"
                + " a arcu justo, consectetur tempor nulla. Curabitur elementum euismod neque, vel adipiscing"
                + " ligula dignissim vel metus. ";

        String over2000Chars = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempor"
                + " feugiat diam vel elementum. Donec magna velit, molestie et vehicula at, vehicula congue"
                + " risus. Curabitur pulvinar convallis metus, sed laoreet augue dapibus non. Nullam pretium"
                + " congue ante id adipiscing. Proin quis consectetur lacus. Sed ac nisl nec tellus pretium"
                + " accumsan. Sed orci massa, volutpat nec tincidunt in, condimentum non ligula. Ut varius"
                + " egestas ullamcorper. Mauris commodo, lorem sed volutpat semper, sem nunc dapibus diam, vitae"
                + " auctor felis massa ac neque. Etiam tempor arcu eu mi aliquet ac scelerisque eros sollicitudin."
                + " Praesent sed est volutpat tellus porttitor suscipit. Suspendisse et quam nec magna porttitor"
                + " vulputate vitae id magna. Suspendisse vestibulum ipsum eget leo molestie imperdiet.  Aenean"
                + " vel justo nisl. Donec felis justo, molestie eu dignissim faucibus, aliquam eu augue. Nullam"
                + " a arcu justo, consectetur tempor nulla. Curabitur elementum euismod neque, vel adipiscing"
                + " feugiat diam vel elementum. Donec magna velit, molestie et vehicula at, vehicula congue"
                + " risus. Curabitur pulvinar convallis metus, sed laoreet augue dapibus non. Nullam pretium"
                + " congue ante id adipiscing. Proin quis consectetur lacus. Sed ac nisl nec tellus pretium"
                + " accumsan. Sed orci massa, volutpat nec tincidunt in, condimentum non ligula. Ut varius"
                + " egestas ullamcorper. Mauris commodo, lorem sed volutpat semper, sem nunc dapibus diam, vitae"
                + " auctor felis massa ac neque. Etiam tempor arcu eu mi aliquet ac scelerisque eros sollicitudin."
                + " Praesent sed est volutpat tellus porttitor suscipit. Suspendisse et quam nec magna porttitor"
                + " vulputate vitae id magna. Suspendisse vestibulum ipsum eget leo molestie imperdiet.  Aenean"
                + " vel justo nisl. Donec felis justo, molestie eu dignissim faucibus, aliquam eu augue. Nullam"
                + " accumsan. Sed orci massa, volutpat nec tincidunt in, condimentum non ligula. Ut varius"
                + " egestas ullamcorper. Mauris commodo, lorem sed volutpat semper, sem nunc dapibus diam, vitae"
                + " auctor felis massa ac neque. Etiam tempor arcu eu mi aliquet ac scelerisque eros sollicitudin."
                + " Praesent sed est volutpat tellus porttitor suscipit. Suspendisse et quam nec magna porttitor"
                + " vulputate vitae id magna. Suspendisse vestibulum ipsum eget leo molestie imperdiet.  Aenean"
                + " vel justo nisl. Donec felis justo, molestie eu dignissim faucibus, aliquam eu augue. Nullam"
                + " a arcu justo, consectetur tempor nulla. Curabitur elementum euismod neque, vel adipiscing"
                + " feugiat diam vel elementum. Donec magna velit, molestie et vehicula at, vehicula congue"
                + " risus. Curabitur pulvinar convallis metus, sed laoreet augue dapibus non. Nullam pretium"
                + " congue ante id adipiscing. Proin quis consectetur lacus. Sed ac nisl nec tellus pretium"
                + " accumsan. Sed orci massa, volutpat nec tincidunt in, condimentum non ligula. Ut varius"
                + " egestas ullamcorper. Mauris commodo, lorem sed volutpat semper, sem nunc dapibus diam, vitae"
                + " auctor felis massa ac neque. Etiam tempor arcu eu mi aliquet ac scelerisque eros sollicitudin."
                + " Praesent sed est volutpat tellus porttitor suscipit. Suspendisse et quam nec magna porttitor"
                + " vulputate vitae id magna. Suspendisse vestibulum ipsum eget leo molestie imperdiet.  Aenean"
                + " vel justo nisl. Donec felis justo, molestie eu dignissim faucibus, aliquam eu augue. Nullam"
                + " a arcu justo, consectetur tempor nulla. Curabitur elementum euismod neque, vel adipisci"
                + " ligula dignissim vel metus. ";

        formData.put("siteLabel", over2000Chars);
        formData.put("contentWarningText", over2000Chars);
        formData.put("termsOfService", over1000Chars);

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
            assertTrue(ve.getErrors().containsKey("siteLabel"));
            assertTrue(ve.getErrors().containsKey("contentWarningText"));
            throw ve;
        }
    }
}
