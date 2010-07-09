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
package org.eurekastreams.web.client.ui.common.stream.thumbnail;

import java.util.LinkedList;

import org.eurekastreams.server.domain.stream.LinkInformation;

/**
 * Thumbnail selector model.
 */
// TODO: Refactor into new widget-model design
public class ThumbnailSelectorCompositeModel
{
    /**
     * List of thumbnails.
     */
    private LinkedList<String> thumbnailUrls = new LinkedList<String>();

    /**
     * Selected thumbnail index.
     */
    private int selectedIndex = 0;

    /**
     * The link.
     */
    private LinkInformation link;

    /**
     * Set the thumbnail.
     *
     * @param inLink
     *            the link.
     */
    public void setLink(final LinkInformation inLink)
    {
        link = inLink;
        selectedIndex = 0;
        thumbnailUrls.clear();

        if (link.getImageUrls().size() > 0)
        {
            for (String imgUrl : link.getImageUrls())
            {
                if (link.getLargestImageUrl().equals(imgUrl))
                {
                    thumbnailUrls.addFirst(imgUrl);
                }
                else
                {
                    thumbnailUrls.add(imgUrl);
                }
            }

            link.setSelectedThumbnail(getSelectedThumbnailUrl());
        }
    }

    /**
     * Get the selected image.
     *
     * @return the URL of the selected image.
     */
    public String getSelectedThumbnailUrl()
    {
        if (selectedIndex < thumbnailUrls.size())
        {
            return thumbnailUrls.get(selectedIndex);
        }
        else
        {
            return "";
        }
    }

    /**
     * @return true if there is a previous thumbnail.
     */
    public boolean hasPrevious()
    {
        return selectedIndex > 0;
    }

    /**
     * Select the previous thumbnail.
     */
    public void selectPrevious()
    {
        selectedIndex--;
    }

    /**
     * @return true if there is a next thumbnail.
     */
    public boolean hasNext()
    {
        return selectedIndex < (thumbnailUrls.size() - 1);
    }

    /**
     * Select the next thumbnail.
     */
    public void selectNext()
    {
        selectedIndex++;
    }

    /**
     * Get the link.
     *
     * @return the link.
     */
    public LinkInformation getLink()
    {
        return link;
    }
}
