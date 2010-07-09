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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Represents a stream plugin's feed.
 *
 */
@SuppressWarnings("serial")
@Entity
public class Feed extends DomainEntity implements Serializable
{
	/**
	 * 
	 */
	@Transient
	private String timeAgo;	
	
    /**
     * This is a list of the subscribers to this feed.
     */
    @SuppressWarnings("unused")
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedId")
    private List<FeedSubscriber> feedSubscribers;	
	
    /**
     * Pending.
     */
    @Basic(optional = false)
    private boolean pending = false;

    /**
     * Url of the feed.
     */
    @Basic(optional = false)
    private String url;
    
    /**
     * Title of the feed.
     */
    @Basic(optional = false)
    private String title;    
    
    /**
     * Date the feed was last updated in total minutes in epoch time.
     */
    @Column(nullable = true)
    private Long updated;
    
    /**
     * Number of minutes we must wait before we can poll the feed again.
     */
    @Column(nullable = true)
    private Long updateFrequency;
    
    /**
     * The latest time for a post.
     */
    @Column(nullable = true)
    private Date lastPostDate;    
    
    /**
     * The plugin the feed belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streamPluginId", nullable = false)
    private PluginDefinition streamPlugin;

    /**
     * Default constructor ensures that the feed subscribers array is inited.
     */
    public Feed()
    {
    	feedSubscribers = new ArrayList<FeedSubscriber>();
    }
    
    /**
     * Set the url.
     * @param inUrl the url.
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }    
    
    /**
     * Gets the URL.
     * @return the url.
     */
    public String getUrl()
    {
    	return url;
    }
    
    /**
     * Gets pending.
     * @return pending.
     */
    public boolean getPending()
    {
    	return pending;
    }
    
    /**
     * Get the plugin associated with the feed.
     * 
     * @return the plugin.
     */
    public PluginDefinition getPlugin()
    {
        return streamPlugin;
    }

    /**
     * Get the last post date of the latest entry.
     * 
     * @return the date.
     */
    public Date getLastPostDate()
    {
        return lastPostDate;
    }

    /**
     * Set the last post date of the latest entry.
     * 
     * @param inDate
     *            the date.
     */
    public void setLastPostDate(final Date inDate)
    {
        lastPostDate = inDate;
    }

    /**
     * Set the last updated time.
     * 
     * @param inUpdated
     *            last updated time.
     */
    public void setLastUpdated(final Long inUpdated)
    {
        updated = inUpdated;
    }

    /**
     * Get the last updated time.
     * 
     * @return the time.
     */
    public Long getLastUpdated()
    {
        return updated; 
    }
    /**
     * Set the update frequency in minutes.
     * 
     * @param inUpdateFrequency
     *            the frequency.
     */
    public void setUpdateFrequency(final Long inUpdateFrequency)
    {
        updateFrequency = inUpdateFrequency;
    }

    /**
     * Sets pending.
     * 
     * @param inPending
     *            pending.
     */
    public void setPending(final boolean inPending)
    {
        pending = inPending;
    }
    

    /**
     * Set the plugin.
     * @param inPlugin the plugin.
     */
    public void setPlugin(final PluginDefinition inPlugin)
    {
        streamPlugin = inPlugin;
    }
    
    /**
     * Gets the people users.
     * 
     * @return the people.
     */
    public List<FeedSubscriber> getFeedSubscribers()
    {
        return feedSubscribers;
    }

    /**
     * Set the title.
     * @param inTitle the title.
     */
    public void setTitle(final String inTitle)
    {
    	title = inTitle;
    }
    
    /**
     * Get the title.
     * @return the title.
     */
    public String getTitle()
    {
    	return title;
    }

    /**
     * Return the time ago.
     * @return the postedTimeAgo
     */
    public String getTimeAgo()
    {
        return timeAgo;
    }
    
    /**
	 * Set the time ago.
	 * @param inTimeAgo the time ago.
     */
    public void setTimeAgo(final String inTimeAgo)
    {
    	timeAgo = inTimeAgo;
    }    
   
}
