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

import junit.framework.Assert;

import org.eurekastreams.web.client.ui.common.stream.decorators.object.BookmarkPopulator;
import org.junit.Test;
import org.eurekastreams.server.domain.stream.LinkInformation;

/**
 * Test for bookmark.
 *
 */
public class BookmarkTest
{
    /**
     * Just test the basic properties and that the populator is of the right type.
     */
    @Test
    public final void test()
    {
        LinkInformation info = new LinkInformation();
        Bookmark sut = new Bookmark(info);

        Assert.assertEquals(info, sut.getLinkInformation());
        Assert.assertTrue(sut.getPopulator() instanceof BookmarkPopulator);
    }
}
