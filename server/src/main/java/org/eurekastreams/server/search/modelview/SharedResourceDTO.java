/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.List;

/**
 * DTO for SharedResource entity.
 * 
 */
public class SharedResourceDTO implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -20174090712845648L;

    /**
     * SharedResource key.
     */
    private String key = "NOT SET";

    /**
     * List of all of the people ids that liked this.
     */
    private List<Long> likerPersonIds;

    /**
     * List of all of the person ids that shared this.
     */
    private List<Long> sharerPersonIds;

    /**
     * A sample of 4 likers - check the like count to see how many there are. -- THIS ISN'T CACHED --
     */
    private List<PersonModelView> likersSample;

    /**
     * A sample of 4 sharers - chekc the shareCount to see how many there are. -- THIS ISN'T CACHED --
     */
    private List<PersonModelView> sharersSample;

    /**
     * Stream scope ID, which is nullable, and will be populated when activity is posted to a shared resource.
     */
    private Long streamScopeId = null;

    /**
     * if the current user liked this resource. -- THIS ISN'T CACHED --
     */
    private boolean isLiked;

    /**
     * @return the likeCount
     */
    public int getLikeCount()
    {
        if (likerPersonIds == null)
        {
            return 0;
        }
        return likerPersonIds.size();
    }

    /**
     * @return the shareCount
     */
    public int getShareCount()
    {
        if (sharerPersonIds == null)
        {
            return 0;
        }
        return sharerPersonIds.size();
    }

    /**
     * @return the likersSample
     */
    public List<PersonModelView> getLikersSample()
    {
        return likersSample;
    }

    /**
     * @param inLikersSample
     *            the likersSample to set
     */
    public void setLikersSample(final List<PersonModelView> inLikersSample)
    {
        likersSample = inLikersSample;
    }

    /**
     * @return the sharersSample
     */
    public List<PersonModelView> getSharersSample()
    {
        return sharersSample;
    }

    /**
     * @param inSharersSample
     *            the sharersSample to set
     */
    public void setSharersSample(final List<PersonModelView> inSharersSample)
    {
        sharersSample = inSharersSample;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @param inKey
     *            the key to set
     */
    public void setKey(final String inKey)
    {
        key = inKey;
    }

    /**
     * @return the streamScopeId
     */
    public Long getStreamScopeId()
    {
        return streamScopeId;
    }

    /**
     * @param inStreamScopeId
     *            the streamScopeId to set
     */
    public void setStreamScopeId(final Long inStreamScopeId)
    {
        streamScopeId = inStreamScopeId;
    }

    /**
     * Set if the current user liked this.
     * 
     * @param inIsLiked
     *            if the current user liked this resource.
     */
    public void setIsLiked(final boolean inIsLiked)
    {
        isLiked = inIsLiked;
    }

    /**
     * @return if the current user liked this resource.
     */
    public boolean isLiked()
    {
        return isLiked;
    }

    /**
     * @return the likerPersonIds
     */
    public List<Long> getLikerPersonIds()
    {
        return likerPersonIds;
    }

    /**
     * @param inLikerPersonIds
     *            the likerPersonIds to set
     */
    public void setLikerPersonIds(final List<Long> inLikerPersonIds)
    {
        likerPersonIds = inLikerPersonIds;
    }

    /**
     * @return the sharerPersonIds
     */
    public List<Long> getSharerPersonIds()
    {
        return sharerPersonIds;
    }

    /**
     * @param inSharerPersonIds
     *            the sharerPersonIds to set
     */
    public void setSharerPersonIds(final List<Long> inSharerPersonIds)
    {
        sharerPersonIds = inSharerPersonIds;
    }
}
