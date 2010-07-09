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
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
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
     * mapper to get group by short name.
     */
    private GetDomainGroupsByShortNames getGroupsByShortNamesMapper = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * {@link UpdateSystemSettingsValidation} system under test.
     */
    private UpdateSystemSettingsValidation sut = new UpdateSystemSettingsValidation(getGroupsByShortNamesMapper);

    /**
     * Test validateParams() with valid inputs.
     */
    @Test
    public void validateParamsWithMap()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("contentExpiration", 1);
        formData.put("termsOfService", "tos");
        formData.put("siteLabel", "sl");
        formData.put("contentWarningText", "cw");
        formData.put("streamViewId", 9L);
        formData.put("supportStreamGroupShortName", "abcdefg");

        context.checking(new Expectations()
        {
            {
                oneOf(getGroupsByShortNamesMapper).fetchUniqueResult("abcdefg");
                will(returnValue(new DomainGroupModelView()));

                allowing(actionContext).getParams();
                will(returnValue(formData));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with bad support group short name.
     */
    @Test(expected = ValidationException.class)
    public void validateParamsWithInvalidSupportGroupShortName()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("contentExpiration", 1);
        formData.put("termsOfService", "tos");
        formData.put("siteLabel", "sl");
        formData.put("contentWarningText", "cw");
        formData.put("streamViewId", 9L);
        formData.put("supportStreamGroupShortName", "abcdefg");

        context.checking(new Expectations()
        {
            {
                oneOf(getGroupsByShortNamesMapper).fetchUniqueResult("abcdefg");
                will(returnValue(null));

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
            assertTrue(ve.getErrors().containsKey("supportStreamGroupShortName"));
            throw ve;
        }
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with empty support group short name (valid).
     */
    @Test()
    public void validateParamsWithEmptySupportGroupShortName()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("contentExpiration", 1);
        formData.put("termsOfService", "tos");
        formData.put("siteLabel", "sl");
        formData.put("contentWarningText", "cw");
        formData.put("streamViewId", 9L);
        formData.put("supportStreamGroupShortName", "");

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test validateParams() with null support group short name (valid).
     */
    @Test()
    public void validateParamsWithNullSupportGroupShortName()
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("contentExpiration", 1);
        formData.put("termsOfService", "tos");
        formData.put("siteLabel", "sl");
        formData.put("contentWarningText", "cw");
        formData.put("streamViewId", 9L);
        formData.put("supportStreamGroupShortName", null);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(formData));
            }
        });

        sut.validate(actionContext);
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
        formData.put("supportEmailAddress", "notvalid");
        formData.put("contentExpiration", -1);
        formData.put("tosPromptInterval", 0);

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
            assertTrue(ve.getErrors().containsKey("supportEmailAddress"));
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
        
        formData.put("siteLabel", over255Chars);
        formData.put("contentWarningText", over255Chars);
        formData.put("termsOfService", over1000Chars);
        formData.put("supportPhoneNumber", over50Chars);
        formData.put("supportEmailAddress", over50Chars);

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
            assertTrue(ve.getErrors().containsKey("supportPhoneNumber"));
            assertTrue(ve.getErrors().containsKey("supportEmailAddress"));
            throw ve;
        }
    }
}
