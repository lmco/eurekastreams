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
package org.eurekastreams.server.domain.stream.plugins;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.EntityType;

/**
 * Feed Subscriber, a person or group who has a feed. Many to Many.
 *
 */
@Entity
public class FeedSubscriber extends DomainEntity implements Serializable
{
	/**
	 * Many feed subscribers can have the same feed, thus many to one here.
	 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedId", nullable = false)
    private Feed feed;
    
    /**
     * Entity id of the subscriber.
     */
    @Basic(optional = false)
    private Long entityId;
    
    /**
     * Type of the subscriber. (PERSON/GROUP).
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private EntityType type;
    
    /**
     * Settings the entity put into the conf page.
     */
    @Basic(optional = true)
    private HashMap<String, Serializable> confSettings;
    
    /**
     * Default constructor. Init the conf settings.
     */
    public FeedSubscriber()
    {
    	confSettings = new HashMap<String, Serializable>();
    }
    
    /**
     * Set the feed.
     * @param inFeed the feed.
     */
    public void setFeed(final Feed inFeed)
    {
    	feed = inFeed;
    }
    
    /**
     * Set the entity.
     * @param inEntityId the entity.
     */
    public void setEntityId(final Long inEntityId)
    {
    	entityId = inEntityId;
    }
    
    /**
     * Set the entity type.
     * @param inEntityType the type.
     */
    public void setEntityType(final EntityType inEntityType)
    {
    	type = inEntityType;
    }
    
    /**
     * Here for serialization.
     * @param inConfSettings the conf settings.
     */
    private void setConfSettings(final HashMap<String, Serializable> inConfSettings)
    {
    	confSettings = inConfSettings;
    }
    
    /**
     * Get the feed.
     * @return the feed.
     */
    public Feed getFeed()
    {
    	return feed;
    }
    
    /**
     * Get the entity id.
     * @return the id.
     */
    public Long getEntityId()
    {
    	return entityId;
    }
    
    /**
     * Get the entity type.
     * @return the type.
     */
    public EntityType getEntityType()
    {
    	return type;
    }
    
    /**
     * Get the conf settings.
     * @return the conf settings.
     */
    public HashMap<String, Serializable> getConfSettings()
    {
    	return confSettings;
    }
}
