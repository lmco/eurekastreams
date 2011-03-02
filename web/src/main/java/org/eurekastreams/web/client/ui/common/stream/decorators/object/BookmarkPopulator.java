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
package org.eurekastreams.web.client.ui.common.stream.decorators.object;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

/**
 * Populates an activityDTO with bookmark properties.
 * 
 */
public class BookmarkPopulator implements ActivityDTOPopulatorStrategy
{
    /**
     * The link info of the bookmark.
     */
    private LinkInformation linkInformation;

    /**
     * Sets the link info.
     * 
     * @param inLinkInformation
     *            the link info.
     */
    public void setLinkInformation(final LinkInformation inLinkInformation)
    {
        linkInformation = inLinkInformation;
    }

    /**
     * Populates the DTO.
     * 
     * @param activity the dto.
     */
    public void populate(final ActivityDTO activity)
    {
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.getBaseObjectProperties().put(StaticResourceBundle.INSTANCE.coreCss().description(),
                linkInformation.getDescription());
        activity.getBaseObjectProperties().put("targetUrl",
                linkInformation.getUrl());
        activity.getBaseObjectProperties().put("targetTitle",
                linkInformation.getTitle());
        activity.getBaseObjectProperties().put(StaticResourceBundle.INSTANCE.coreCss().thumbnail(),
                linkInformation.getSelectedThumbnail());

    }

}
