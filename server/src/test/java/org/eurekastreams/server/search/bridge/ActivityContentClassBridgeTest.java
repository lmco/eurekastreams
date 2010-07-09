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
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.junit.Test;

/**
 * Test fixture for ActivityContentClassBridge.
 */
public class ActivityContentClassBridgeTest
{
    /**
     * System under test.
     */
    private ActivityContentClassBridge sut = new ActivityContentClassBridge();

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
    public void testObjectToStringFromActivityNote()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.NOTE);
        activity.setBaseObject(baseObject);

        assertEquals(content, sut.objectToString(activity));
    }

    /**
     * Test objectToString() when base object type is Note and there is content.
     */
    @Test
    public void testObjectToStringFromActivityNoteWithNoContent()
    {
        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.NOTE);
        assertNull(sut.objectToString(activity));
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there is content, description, and title.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithContentAndDescriptionAndTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("description", description);
        baseObject.put("targetTitle", targetTitle);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        assertEquals(content + " " + targetTitle + " " + description, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there is content and description, but no title.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithContentAndDescription()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("description", description);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        assertEquals(content + " " + description, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there is content and description, but no title.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithContentAndTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("targetTitle", targetTitle);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        assertEquals(content + " " + targetTitle, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there is content and description, but no title.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithDescriptionAndTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);
        baseObject.put("description", description);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        assertEquals(content + " " + description, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there is content only.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithContent()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        assertEquals(content, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there is description only.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithDescription()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("description", description);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        assertEquals(description, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there is title only.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithTitle()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("targetTitle", targetTitle);

        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        assertEquals(targetTitle, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there's no base object.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithNoBaseObject()
    {
        Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        assertNull(sut.objectToString(activity));
    }
}
