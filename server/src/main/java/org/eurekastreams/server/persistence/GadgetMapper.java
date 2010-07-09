/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.Gadget;

/**
 * This class provides the mapper functionality for Gadget entities.
 */
public class GadgetMapper extends DomainEntityMapper<Gadget>
{
    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public GadgetMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }
    
    /**
     * Get the domain entity name for the generic query operations.
     * 
     * @return the domain entity name for the gadget query operations.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Gadget";
    }

}
