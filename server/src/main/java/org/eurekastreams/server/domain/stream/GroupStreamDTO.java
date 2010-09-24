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
package org.eurekastreams.server.domain.stream;

import java.io.Serializable;

/**
 * Represents the minimal information needed to display Group Stream data in the UI.
 */
public class GroupStreamDTO implements Serializable, StreamFilter
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 5019586426204970270L;

    /**
     * The id of the group.
     */
    private long id;

    /**
     * The short name for the group.
     */
    private String shortName;

    /**
     * The full name for the group.
     */
    private String name;

    /**
     * Is group stream postable.
     */
    private boolean isPostable;

    /**
     * Constructor.
     *
     * @param inId
     *            the id to set.
     * @param inName
     *            the name to set.
     * @param inShortName
     *            the shortName to set.
     * @param inIsPostable
     *            if the stream can have activities posted to it.
     */
    public GroupStreamDTO(final long inId, final String inName, final String inShortName, final boolean inIsPostable)
    {
        id = inId;
        shortName = inShortName;
        name = inName;
        isPostable = inIsPostable;
    }

    /**
     * Default constructor necessary for classes serialized to the client.
     */
    protected GroupStreamDTO()
    {
        // Intentionally empty.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String inName)
    {
        name = inName;
    }

    /**
     * @return the shortName
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * @param inShortName
     *            the shortName to set
     */
    public void setShortName(final String inShortName)
    {
        shortName = inShortName;
    }

    /**
     * @return the isPostable
     */
    public boolean isPostable()
    {
        return isPostable;
    }

    /**
     * @param inIsPostable
     *            the isPostable to set
     */
    public void setPostable(final boolean inIsPostable)
    {
        isPostable = inIsPostable;
    }
}
