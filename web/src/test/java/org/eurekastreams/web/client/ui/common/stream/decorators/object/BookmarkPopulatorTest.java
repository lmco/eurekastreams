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

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.LinkInformation;

/**
 * Test the bookmark populator.
 *
 */
public class BookmarkPopulatorTest
{
    /**
     * Test for the bookmark populate.
     */
    @Test
    public final void populate()
    {
        LinkInformation info = new LinkInformation();
        info.setDescription("description");
        info.setUrl("url");
        info.setTitle("title");
        info.setSelectedThumbnail("thumbnail");

        BookmarkPopulator sut = new BookmarkPopulator();
        sut.setLinkInformation(info);

        ActivityDTO activity = new ActivityDTO();
        activity.setBaseObjectProperties(new HashMap<String, String>());
        sut.populate(activity);

        Assert.assertEquals(BaseObjectType.BOOKMARK, activity
                .getBaseObjectType());
        Assert.assertEquals("description", activity.getBaseObjectProperties()
                .get("description"));
        Assert.assertEquals("url", activity.getBaseObjectProperties().get(
                "targetUrl"));
        Assert.assertEquals("title", activity.getBaseObjectProperties().get(
                "targetTitle"));
        Assert.assertEquals("thumbnail", activity.getBaseObjectProperties()
                .get("thumbnail"));
    }
}
