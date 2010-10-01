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
package org.eurekastreams.server.action.validation.stream;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.stream.Stream;
import org.junit.Test;

/**
 * Tests the custom stream modification validation strategy.
 */
public class ModifyStreamForCurrentUserValidationTest
{
    /**
     * System under test.
     */
    private ModifyStreamForCurrentUserValidation sut = new ModifyStreamForCurrentUserValidation();

    /**
     * Tests with valid parameters, no exception.
     */
    @Test
    public void testValid()
    {
        Stream stream = new Stream();
        stream.setName("something");

        PrincipalActionContext actionContext = new ServiceActionContext(stream, null);

        sut.validate(actionContext);
    }

    /**
     * Tests with invalid parameters, empty string.
     */
    @Test(expected = ValidationException.class)
    public void testInvalidEmpty()
    {
        Stream stream = new Stream();
        stream.setName("");

        PrincipalActionContext actionContext = new ServiceActionContext(stream, null);

        sut.validate(actionContext);
    }

    /**
     * Tests with invalid parameters, null string.
     */
    @Test(expected = ValidationException.class)
    public void testInvalidNull()
    {
        Stream stream = new Stream();
        stream.setName(null);

        PrincipalActionContext actionContext = new ServiceActionContext(stream, null);

        sut.validate(actionContext);
    }
}
