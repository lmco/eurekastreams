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
package org.eurekastreams.server.domain.stream;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.server.domain.Person;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;

/**
 * Entity to represent search of a StreamView.
 *
 */
@Entity
public class StreamSearch extends DomainEntity implements Serializable, StreamFilter
{

    /**
     * Serial Version id.
     */
    private static final long serialVersionUID = -1115288887068723696L;

    /**
     * The streamView to search.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Basic(optional = false)
    private StreamView streamView;

    /**
     * Private reference back to the person for queries originating with the StreamSearch.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "person_streamsearch", joinColumns = @JoinColumn(name = "streamsearches_id"),
    // line break
    inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Person person;

    /**
     * Get the owner of this stream search.
     *
     * @return the owner of the stream search
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * Keywords to use for search.
     */
    @Cascade(
    // line break
    { org.hibernate.annotations.CascadeType.ALL })
    @CollectionOfElements(targetElement = String.class, fetch = FetchType.EAGER)
    private Set<String> keywords;

    /**
     * Name of StreamSearch.
     */
    @Basic(optional = false)
    private String name;

    /**
     * Constructor for ORM.
     */
    @SuppressWarnings("unused")
    private StreamSearch()
    {
        // no-op.
    }

    /**
     * Constructor.
     *
     * @param inName
     *            The name of the StreamSearch.
     * @param inStreamView
     *            The {@link StreamView}.
     * @param inKeywords
     *            The set of keywords to use for search.
     */
    public StreamSearch(final String inName, final StreamView inStreamView, final Set<String> inKeywords)
    {
        streamView = inStreamView;
        keywords = inKeywords;
        name = inName;
    }

    /**
     * @return the streamView
     */
    public StreamView getStreamView()
    {
        return streamView;
    }

    /**
     * @param inStreamView
     *            the streamView to set
     */
    public void setStreamView(final StreamView inStreamView)
    {
        this.streamView = inStreamView;
    }

    /**
     * @return the keywords
     */
    public Set<String> getKeywords()
    {
        return keywords;
    }

    /**
     * @param inKeywords
     *            the keywords to set
     */
    public void setKeywords(final Set<String> inKeywords)
    {
        this.keywords = inKeywords;
    }

    /**
     * Sets the name of the search.
     *
     * @param inName
     *            the name.
     */
    public void setName(final String inName)
    {
        this.name = inName;
    }

    /**
     * Gets the name of the search.
     *
     * @return the name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets the list of keywords as a space-delimited string.
     *
     * @return the list of keywords in string format.
     */
    public String getKeywordsAsString()
    {
        String keywordsString = "";
        if (keywords.size() > 0)
        {
            for (String keyword : keywords)
            {
                keywordsString += keyword + " ";
            }

            // trims the trailing space
            return keywordsString.substring(0, keywordsString.length() - 1);
        }
        return keywordsString;
    }
}
