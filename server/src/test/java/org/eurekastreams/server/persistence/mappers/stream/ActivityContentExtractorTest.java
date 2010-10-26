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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.junit.Test;

/**
 * Test fixture for ActivityContentExtractor.
 */
public class ActivityContentExtractorTest
{
    /**
     * System under test.
     */
    private final ActivityContentExtractor sut = new ActivityContentExtractor();

    /**
     * Description.
     */
    private final String description = "sdlkfjsdlkjfsdjfsdkfsdfklsdjfsdjf";

    /**
     * Content.
     */
    private final String content = "sldkjf lkjsdf sldkjf sldkfj sdlfkj sdfjkl";

    /**
     * Title.
     */
    private final String targetTitle = "38skjsdlkj 2likjsd";

    /**
     * Test objectToString() when base object type is Note and there is content.
     */
    @Test
    public void testobjectToStringFromActivityNote()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);

        assertEquals(content, sut.extractContent(BaseObjectType.NOTE, baseObject));
    }

    /**
     * Test objectToString() when base object type is Note and there is content.
     */
    @Test
    public void testobjectToStringFromActivityNoteWithNoContent()
    {
        Activity activity = new Activity();
        assertNull(sut.extractContent(BaseObjectType.NOTE, null));
    }

    /**
     * Test objectToString() when base type object is Bookmark and there is content, description, and title.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithContentAndDescriptionAndTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("description", description);
        baseObject.put("targetTitle", targetTitle);

        assertEquals(content + " " + targetTitle + " " + description, sut.extractContent(BaseObjectType.BOOKMARK,
                baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is Bookmark and there is content and description, but no title.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithContentAndDescription()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("description", description);

        assertEquals(content + " " + description, sut.extractContent(BaseObjectType.BOOKMARK, baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is Bookmark and there is content and description, but no title.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithContentAndTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("targetTitle", targetTitle);

        assertEquals(content + " " + targetTitle, sut.extractContent(BaseObjectType.BOOKMARK, baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is Bookmark and there is content and description, but no title.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithDescriptionAndTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("description", description);

        assertEquals(content + " " + description, sut.extractContent(BaseObjectType.BOOKMARK, baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is Bookmark and there is content only.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithContent()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);

        assertEquals(content, sut.extractContent(BaseObjectType.BOOKMARK, baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is Bookmark and there is description only.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithDescription()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("description", description);

        assertEquals(description, sut.extractContent(BaseObjectType.BOOKMARK, baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is Bookmark and there is title only.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("targetTitle", targetTitle);

        assertEquals(targetTitle, sut.extractContent(BaseObjectType.BOOKMARK, baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is Bookmark and there's no base object.
     */
    @Test
    public void testobjectToStringFromActivityBookmarkWithNoBaseObject()
    {
        assertNull(sut.extractContent(BaseObjectType.BOOKMARK, null));
    }

    /**
     * Test objectToString() when base type object is File and there is a title.
     */
    @Test
    public void testobjectToStringFromActivityFileWithTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("targetTitle", targetTitle);

        assertEquals(targetTitle, sut.extractContent(BaseObjectType.FILE, baseObject).trim());
    }

    /**
     * Test objectToString() when base type object is File and there is no title.
     */
    @Test
    public void testobjectToStringFromActivityFileWithoutTitle()
    {
        assertNull(sut.extractContent(BaseObjectType.FILE, new HashMap<String, String>()));
    }

    /**
     * Test objectToString() when base type object is File and there's no base object.
     */
    @Test
    public void testobjectToStringFromActivityFileWithNoBaseObject()
    {
        assertNull(sut.extractContent(BaseObjectType.FILE, null));
    }

}
