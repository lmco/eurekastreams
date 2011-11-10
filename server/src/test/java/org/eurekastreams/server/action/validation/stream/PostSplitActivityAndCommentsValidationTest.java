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
package org.eurekastreams.server.action.validation.stream;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.stream.PostSplitActivityAndCommentsRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests PostSplitActivityAndCommentsValidation.
 */
public class PostSplitActivityAndCommentsValidationTest
{
    /** SUT. */
    private ValidationStrategy<ActionContext> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new PostSplitActivityAndCommentsValidation();
    }

    /**
     * Test.
     */
    @Test
    public void testValid()
    {
        sut.validate(TestContextCreator.createPrincipalActionContext(new PostSplitActivityAndCommentsRequest(null, 0,
                "text"), null));
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testNull()
    {
        sut.validate(TestContextCreator.createPrincipalActionContext(null, null));
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testNullText()
    {
        sut.validate(TestContextCreator.createPrincipalActionContext(new PostSplitActivityAndCommentsRequest(null, 0,
                null), null));
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testEmptyText()
    {
        sut.validate(TestContextCreator.createPrincipalActionContext(new PostSplitActivityAndCommentsRequest(null, 0,
                ""), null));
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testBlankText()
    {
        sut.validate(TestContextCreator.createPrincipalActionContext(new PostSplitActivityAndCommentsRequest(null, 0,
                " \t  "), null));
    }
}
