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

import javax.persistence.Entity;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NaturalId;

/**
 * HashTag in an Activity.
 */
@Entity
public class HashTag extends DomainEntity implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 3296177548196350130L;

    /**
     * The content of the hashtag, including the #.
     */
    @Index(name="hashtag_content_idx")
    @NaturalId
    private String content;

    /**
     * Empty constructor.
     */
    public HashTag()
    {
    }

    /**
     * Constructor taking the content.
     *
     * @param inContent
     *            the content of the hashtag
     */
    public HashTag(final String inContent)
    {
        if (!inContent.startsWith("#"))
        {
            content = "#" + inContent.toLowerCase();
        }
        else
        {
            content = inContent.toLowerCase();
        }
    }

    /**
     * Get the content of the hashtag, including the #.
     *
     * @return the content of the hashtag, including the #
     */
    public String getContent()
    {
        return content;
    }

    /**
     * Set the content of the hashtag, including the #.
     *
     * @param inContent
     *            the content of the hashtag, including the #
     */
    public void setContent(final String inContent)
    {
        content = inContent;
    }
}
