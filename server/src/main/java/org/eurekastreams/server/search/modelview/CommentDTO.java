/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.modelview;

import java.util.Date;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;

/**
 * DTO object for Activity comments.
 *
 */
public class CommentDTO extends ModelView
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 4827478615071727280L;

    /**
     * id of the comment.
     */
    private long id;

    /**
     * The avatar id.
     */
    private String authorAvatarId = UNINITIALIZED_STRING_VALUE;

    /**
     * Entity id for author.
     */
    private long authorId = UNINITIALIZED_LONG_VALUE;

    /**
     * Author account id.
     */
    private String authorAccountId = UNINITIALIZED_STRING_VALUE;

    /**
     * Author display name.
     */
    private String authorDisplayName = UNINITIALIZED_STRING_VALUE;

    /**
     * Comment text.
     */
    private String body = UNINITIALIZED_STRING_VALUE;

    /**
     * Time comment was sent.
     */
    private Date timeSent = UNINITIALIZED_DATE_VALUE;

    /**
     * Activity id this comment is associated with.
     */
    private long activityId = UNINITIALIZED_LONG_VALUE;

    /**
     * boolean representing if the comment able to be deleted by user.
     */
    private Boolean deletable = false;

    /** If the comment author's account is active. */
    private boolean authorActive = true;

    /**
     * Load this object's properties from the input Map.
     *
     * @param properties
     *            the Map of the properties to load
     */
    @Override
    public void loadProperties(final Map<String, Object> properties)
    {
        super.loadProperties(properties);

        if (properties.containsKey("id"))
        {
            setId((Long) properties.get("id"));
        }
        if (properties.containsKey("activityId"))
        {
            setActivityId((Long) properties.get("activityId"));
        }
        if (properties.containsKey("authorAccountId"))
        {
            setAuthorAccountId((String) properties.get("authorAccountId"));
        }
        if (properties.containsKey("authorAvatarId"))
        {
            this.setAuthorAvatarId((String) properties.get("authorAvatarId"));
        }
        if (properties.containsKey("authorDisplayName"))
        {
            setAuthorDisplayName((String) properties.get("authorDisplayName"));
        }
        if (properties.containsKey("authorId"))
        {
            setAuthorId((Long) properties.get("authorId"));
        }
        if (properties.containsKey("body"))
        {
            setBody((String) properties.get("body"));
        }
        if (properties.containsKey("timeSent"))
        {
            setTimeSent((Date) properties.get("timeSent"));
        }
        if (properties.containsKey("deletable"))
        {
            setDeletable((Boolean) properties.get("deletable"));
        }
    }

    /**
     * Return entity name this DTO represents.
     * @return The entity name this DTO represents.
     */
    @Override
    protected String getEntityName()
    {
        return "Comment";
    }

    /**
     * @return the authorAvatarId
     */
    public String getAuthorAvatarId()
    {
        return authorAvatarId;
    }

    /**
     * @param inAuthorAvatarId the authorAvatarId to set
     */
    public void setAuthorAvatarId(final String inAuthorAvatarId)
    {
        this.authorAvatarId = inAuthorAvatarId;
    }

    /**
     * @return the authorAccountId
     */
    public String getAuthorAccountId()
    {
        return authorAccountId;
    }

    /**
     * @param inAuthorAccountId the authorAccountId to set
     */
    public void setAuthorAccountId(final String inAuthorAccountId)
    {
        this.authorAccountId = inAuthorAccountId;
    }

    /**
     * @return the authorDisplayName
     */
    public String getAuthorDisplayName()
    {
        return authorDisplayName;
    }

    /**
     * @param inAuthorDisplayName the authorDisplayName to set
     */
    public void setAuthorDisplayName(final String inAuthorDisplayName)
    {
        this.authorDisplayName = inAuthorDisplayName;
    }

    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    /**
     * @param inBody the body to set
     */
    public void setBody(final String inBody)
    {
        this.body = inBody;
    }

    /**
     * @return the timeSent
     */
    public Date getTimeSent()
    {
        return timeSent;
    }

    /**
     * Set when the comment was added to the system.
     * @param inTimeSent - value for time sent.
     */
    public void setTimeSent(final Date inTimeSent)
    {
        this.timeSent = inTimeSent;
    }

    /**
     * @return the authorId
     */
    public long getAuthorId()
    {
        return authorId;
    }

    /**
     * @param inAuthorId the authorId to set
     */
    public void setAuthorId(final long inAuthorId)
    {
        this.authorId = inAuthorId;
    }

    /**
     * @return the activityId
     */
    public long getActivityId()
    {
        return activityId;
    }

    /**
     * @param inActivityId the activityId to set
     */
    public void setActivityId(final long inActivityId)
    {
        this.activityId = inActivityId;
    }

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId the id to set
     */
    public void setId(final long inId)
    {
        this.id = inId;
    }

    /**
     * @return the deletable
     */
    public Boolean isDeletable()
    {
        return deletable;
    }

    /**
     * @param inDeletable the deletable to set
     */
    public void setDeletable(final Boolean inDeletable)
    {
        this.deletable = inDeletable;
    }

    /**
     * @return If the comment author's account is active.
     */
    public boolean isAuthorActive()
    {
        return authorActive;
    }

    /**
     * @param inAuthorActive
     *            If the comment author's account is active.
     */
    public void setAuthorActive(final boolean inAuthorActive)
    {
        authorActive = inAuthorActive;
    }
}
