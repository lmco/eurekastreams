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
package org.eurekastreams.server.action.validation.notification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.testing.TestContextCreator;
import org.junit.Test;

/**
 * Tests DisableNotificationCategoryValidation.
 */
public class DisableNotificationCategoryValidationTest
{
    /** Test data: valid categories. */
    private static final Set<String> CATEGORIES = new HashSet<String>(Arrays.asList("COMMENT", "LIKE"));

    /**
     * Tests Validate.
     */
    @Test
    public void testValidateValid()
    {
        ValidationStrategy<ActionContext> sut = new DisableNotificationCategoryValidation(CATEGORIES);
        ActionContext ctx = TestContextCreator.createPrincipalActionContext("LIKE", null);
        sut.validate(ctx);
    }

    /**
     * Tests Validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateInvalid()
    {
        ValidationStrategy<ActionContext> sut = new DisableNotificationCategoryValidation(CATEGORIES);
        ActionContext ctx = TestContextCreator.createPrincipalActionContext("NOSUCH", null);
        sut.validate(ctx);
    }
}
