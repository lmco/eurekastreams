/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence;

import org.eurekastreams.commons.hibernate.QueryOptimizer;

/**
 * Support class for unit testing.
 * 
 */
public class PersistentLoginMapperSubClassSupport extends PersistentLoginMapper
{
    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use
     */
    public PersistentLoginMapperSubClassSupport(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Gets the domain entity name.
     * 
     * @return The domain entity name.
     */
    protected String getDomainEntityName()
    {
        return super.getDomainEntityName();
    }
}
