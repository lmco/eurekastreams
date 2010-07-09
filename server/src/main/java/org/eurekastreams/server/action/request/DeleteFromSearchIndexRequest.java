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
package org.eurekastreams.server.action.request;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Request object for purging info from search index.
 * 
 */
public class DeleteFromSearchIndexRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -8166030719582349572L;

    /**
     * Class type.
     */
    private Class< ? > clazz;

    /**
     * Ids to purge.
     */
    private List<Long> ids;

    /**
     * Private default constructor.
     */
    @SuppressWarnings("unused")
    private DeleteFromSearchIndexRequest()
    {
        // no-op
    }

    /**
     * Constructor.
     * 
     * @param inClazz
     *            Class type.
     * @param inIds
     *            Ids to purge.
     */
    public DeleteFromSearchIndexRequest(final Class< ? > inClazz, final List<Long> inIds)
    {
        clazz = inClazz;
        ids = inIds;
    }

    /**
     * Constructor.
     * 
     * @param inClazz
     *            Class type.
     * @param inId
     *            Id to purge.
     */
    public DeleteFromSearchIndexRequest(final Class< ? > inClazz, final Long inId)
    {
        clazz = inClazz;
        ids = Arrays.asList(inId);
    }

    /**
     * @return the clazz
     */
    public Class< ? > getClazz()
    {
        return clazz;
    }

    /**
     * @return the ids
     */
    public List<Long> getIds()
    {
        return ids;
    }

    /**
     * @param inClazz
     *            the clazz to set
     */
    @SuppressWarnings("unused")
    private void setClazz(final Class< ? > inClazz)
    {
        clazz = inClazz;
    }

    /**
     * @param inIds
     *            the ids to set
     */
    @SuppressWarnings("unused")
    private void setIds(final List<Long> inIds)
    {
        ids = inIds;
    }

}
