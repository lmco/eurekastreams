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
package org.eurekastreams.commons.actions.context;

/**
 * This class is a simple implementation of the {@link PrincipalPopulator} interface
 * for the {@link DefaultPrincipal} class.
 *
 */
public final class DefaultPrincipalPopulator implements PrincipalPopulator
{
    /**
     * Default constructor implementation.
     */
    private DefaultPrincipalPopulator()
    {
        //default constructor, nothing to do here.
    }

    /**
     * This is a simple getter for the Principal object.  Offered as an example on how to
     * implement this interface.
     *
     * The id is passed in as null within this implementation.
     * {@inheritDoc}
     */
    public Principal getPrincipal(final String inAccountId)
    {
        return new DefaultPrincipal(inAccountId, inAccountId, null);
    }
}
