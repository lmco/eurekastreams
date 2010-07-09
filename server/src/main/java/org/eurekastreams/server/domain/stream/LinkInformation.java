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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.CollectionOfElements;

/**
 * Link Information.
 */
@Entity
public class LinkInformation extends DomainEntity implements Serializable
{

    /**
     * Max URL lenght, longer URLs will not work in IE. Other browsers can support longer in theory, but this seemed
     * like a reasonable place to draw the line.
     */
    private static final int MAX_URL_LENGTH = 2048;

    /**
     * Serial Version ID.
     */
    private static final long serialVersionUID = -5954974803485135904L;

    /**
     * The title.
     */
    @Basic
    private String title = "";

    /**
     * The source.
     */
    @Basic
    private String source = "";

    /**
     * The url.
     */
    @Column(length = MAX_URL_LENGTH, nullable = false)
    private String url = "";

    /**
     * The largest image URL.
     */
    @Basic
    private String largestImageUrl = "";

    /**
     * The image URLs.
     */
    @CollectionOfElements(targetElement = String.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "linkInformationId")
    private Set<String> imageUrls = new HashSet<String>();;

    /**
     * The description.
     */
    @Basic
    private String description = "";

    /**
     * The selected thumbnail.
     */
    @Transient
    private String selectedThumbnail = "";

    /**
     * The date the link information was created.
     */
    @Basic
    private Date created;

    /**
     * Public Constructor.
     */
    public LinkInformation()
    {

    }

    /**
     * @param inTitle
     *            the title to set
     */
    public void setTitle(final String inTitle)
    {
        this.title = inTitle;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param inImageUrls
     *            the imageUrls to set
     */
    public void setImageUrls(final Set<String> inImageUrls)
    {
        this.imageUrls = inImageUrls;
    }

    /**
     * @return the imageUrls
     */
    public Set<String> getImageUrls()
    {
        return imageUrls;
    }

    /**
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        this.description = inDescription;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param inUrl
     *            the url to set
     */
    public void setUrl(final String inUrl)
    {
        this.url = inUrl;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Get the link information as HTML.
     * TODO Pull out display logic.
     * @return the HTML.
     */
    public String getHtml()
    {
        String html = "<div class='message-link has-thumbnail'><div>";

        if (getSelectedThumbnail().length() > 0)
        {
            html += "<img class='thumbnail' src='" + getSelectedThumbnail() + "'></div>";
        }

        html += "<div><a class='title' href='" + getUrl() + "'>" + getTitle() + "</a>"
                + "</div><div class='url'>source: <a href=\"" + getSource() + "\">" + getSource() + "</a></div>"
                + "<div class='gwt-Label meta-description'>" + getDescription() + "</div></div>";

        return html;
    }

    /**
     * @param inSelectedThumbnail
     *            the selectedThumbnail to set
     */
    public void setSelectedThumbnail(final String inSelectedThumbnail)
    {
        this.selectedThumbnail = inSelectedThumbnail;
    }

    /**
     * @return the selectedThumbnail
     */
    public String getSelectedThumbnail()
    {
        return selectedThumbnail;
    }

    /**
     * @param inCreated
     *            the created to set.
     */
    public void setCreated(final Date inCreated)
    {
        this.created = inCreated;
    }

    /**
     * @return the created.
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * @param inLargestImageUrl
     *            the largestImageUrl to set.
     */
    public void setLargestImageUrl(final String inLargestImageUrl)
    {
        this.largestImageUrl = inLargestImageUrl;
    }

    /**
     * @return the largestImageUrl.
     */
    public String getLargestImageUrl()
    {
        return largestImageUrl;
    }

    /**
     * @return the source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * @param inSource
     *            the source to set
     */
    public void setSource(final String inSource)
    {
        this.source = inSource;
    }
}
