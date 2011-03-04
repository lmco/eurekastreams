/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.attach.bookmark;

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.BookmarkPopulator;

/**
 * This class represents a bookmark attachment. It SHOULD contain all the fields
 * in link information but that is being saved until a future story. TODO: Move
 * all the fields from link information into here.
 * 
 */
public class Bookmark implements Attachment
{
    /**
     * The information on the link.
     */
    private LinkInformation linkInformation;

    /**
     * Default constructor.
     * 
     * @param inLinkInformation
     *            the link information.
     */
    public Bookmark(final LinkInformation inLinkInformation)
    {
        linkInformation = inLinkInformation;
    }

    /**
     * Gets the link info.
     * 
     * @return the link info.
     */
    public LinkInformation getLinkInformation()
    {
        return linkInformation;
    }

    /**
     * Gets the populator.
     * @return the populator.
     */
    public ActivityDTOPopulatorStrategy getPopulator()
    {
        BookmarkPopulator bookmarkDecorator = new BookmarkPopulator();
        bookmarkDecorator.setLinkInformation(linkInformation);
        return bookmarkDecorator;
    }

}
