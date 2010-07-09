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
package org.eurekastreams.server.persistence.mappers;

import org.eurekastreams.commons.hibernate.QueryOptimizer;

/**
 * Abstract DomainEntityMapper, allows for single spot for EntityManager injection so all mappers don't need to
 * duplicate the template code.
 * 
 * @param <TRequestType>
 *            MapperRequest type for the mapper.
 * @param <TReturnType>
 *            Return type for the mapper.
 */
public abstract class ReadMapper<TRequestType, TReturnType>  
    extends BaseArgDomainMapper<TRequestType, TReturnType>
{
    /**
     * The QueryOptimizer to use for specialized functions.
     */
    private QueryOptimizer queryOptimizer;

    /**
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for caching.
     */
    public void setQueryOptimizer(final QueryOptimizer inQueryOptimizer)
    {
        this.queryOptimizer = inQueryOptimizer;
    }

    /**
     * Get the QueryOptimizer to use for specialized functions.
     * 
     * @return the QueryOptimizer to use for specialized functions.
     */
    protected QueryOptimizer getQueryOptimizer()
    {
        return queryOptimizer;
    }

}
