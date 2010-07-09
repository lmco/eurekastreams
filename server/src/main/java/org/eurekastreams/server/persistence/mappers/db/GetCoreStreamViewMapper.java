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
package org.eurekastreams.server.persistence.mappers.db;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Simple mapper to retrieve the StreamView object that represents the supplied Stream Type..
 *
 */
public class GetCoreStreamViewMapper extends BaseArgDomainMapper<Type, StreamView>
{

    /**
     * Retrieve the StreamView object that represents the Stream Type. {@inheritDoc}.
     */
    @Override
    public StreamView execute(final Type inType)
    {
        if (inType.equals(Type.NOTSET))
        {
            throw new IllegalArgumentException("This mapper does not support the supplied StreamView Type.");
        }

        Query q = getEntityManager().createQuery("from StreamView sv where sv.type =:type")
                .setParameter("type", inType);

        return (StreamView) q.getSingleResult();
    }

}
