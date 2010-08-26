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

import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;

/**
 * Stores security information about an Activity. Used to determine who can see the activity.
 */
public class ActivitySecurityDTO extends ModelView
{
    /**
     * Version.
     */
    private static final long serialVersionUID = -1982680235906203165L;

    /**
     * If the destination stream is public.
     */
    private boolean isDestinationStreamPublic;

    /**
     * The activity ID.
     */
    private Long id;

    /**
     * Destination stream ID.
     */
    private Long destinationStreamId;

    /**
     * Destination Entity ID.
     */
    private Long destinationEntityId;

    /**
     * Set if the destination stream is public.
     * 
     * @param inIsDestinationStreamPublic
     *            the if destination stream is public.
     */
    public void setDestinationStreamPublic(final boolean inIsDestinationStreamPublic)
    {
        this.isDestinationStreamPublic = inIsDestinationStreamPublic;
    }

    /**
     * @return true if the destination stream is public, false if not.
     */
    public boolean isDestinationStreamPublic()
    {
        return isDestinationStreamPublic;
    }

    /**
     * Set the destination entity ID.
     * 
     * @param inDestinationEntityId
     *            the destination entity ID.
     */
    public void setDestinationEntityId(final Long inDestinationEntityId)
    {
        this.destinationEntityId = inDestinationEntityId;
    }

    /**
     * @return the destination entity ID.
     */
    public Long getDestinationEntityId()
    {
        return destinationEntityId;
    }

    /**
     * Set the destination stream ID.
     * 
     * @param inDestinationStreamId
     *            the destination stream ID.
     */
    public void setDestinationStreamId(final Long inDestinationStreamId)
    {
        this.destinationStreamId = inDestinationStreamId;
    }

    /**
     * @return the destination stream ID.
     */
    public Long getDestinationStreamId()
    {
        return destinationStreamId;
    }

    /**
     * Set the activity ID.
     * 
     * @param inId
     *            the id.
     */
    public void setId(final Long inId)
    {
        this.id = inId;
    }

    /**
     * @return the activity id.
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Load properties.
     * 
     * @param properties
     *            the properties.
     */
    @Override
    public void loadProperties(final Map<String, Object> properties)
    {
        super.loadProperties(properties);

        if (properties.containsKey("isDestinationStreamPublic"))
        {
            setDestinationStreamPublic((Boolean) properties.get("isDestinationStreamPublic"));
        }
        if (properties.containsKey("id"))
        {
            setId((Long) properties.get("id"));
        }
        if (properties.containsKey("destinationStreamId"))
        {
            setDestinationStreamId((Long) properties.get("destinationStreamId"));
        }
        if (properties.containsKey("destinationEntityId"))
        {
            setDestinationEntityId((Long) properties.get("destinationEntityId"));
        }

    }

    /**
     * Get the entity name.
     * 
     * @return the entity name.
     */
    protected String getEntityName()
    {
        return "ActivitySecurityDTO";
    }
}
