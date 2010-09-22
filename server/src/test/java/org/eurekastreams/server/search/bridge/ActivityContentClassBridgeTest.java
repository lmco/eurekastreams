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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.stream.GetCommentsById;
import org.eurekastreams.server.persistence.mappers.stream.GetOrderedCommentIdsByActivityId;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test fixture for ActivityContentClassBridge.
 */
public class ActivityContentClassBridgeTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

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
     * Get comment IDs DAO.
     */
    private static GetOrderedCommentIdsByActivityId commentIdsByActivityIdDAO = CONTEXT
            .mock(GetOrderedCommentIdsByActivityId.class);

    /**
     * Get comments DAO.
     */
    private static GetCommentsById commentsByIdDAO = CONTEXT.mock(GetCommentsById.class);

    /**
     * Setup test fixtures.
     */
    @BeforeClass
    public static void setup()
    {
        ActivityContentClassBridge.setCommentDAOs(commentIdsByActivityIdDAO, commentsByIdDAO);
    }

    /**
     * Test objectToString() when base object type is Note and there is content.
     */
    @Test
    public void testObjectToStringFromActivityNote()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("content", content);

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.NOTE);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

        assertEquals(content, sut.objectToString(activity));
    }

    /**
     * Test objectToString() when base object type is Note and there is content.
     */
    @Test
    public void testObjectToStringFromActivityNoteWithNoContent()
    {
        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.NOTE);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

        assertEquals("", sut.objectToString(activity));
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

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

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

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

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

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

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

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

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

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

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

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

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

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

        assertEquals(targetTitle, sut.objectToString(activity).trim());
    }

    /**
     * Test objectToSTring() when base type object is Bookmark and there's no base object.
     */
    @Test
    public void testObjectToStringFromActivityBookmarkWithNoBaseObject()
    {
        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(new ArrayList<Long>()));

                oneOf(commentsByIdDAO).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<CommentDTO>()));
            }
        });

        assertEquals("", sut.objectToString(activity));
    }

    /**
     * Test objectToString() when there are comments.
     */
    @Test
    public void testObjectToStringWithComment()
    {
        HashMap<String, String> baseObject = new HashMap<String, String>();
        baseObject.put("description", description);

        final Activity activity = new Activity();
        activity.setBaseObjectType(BaseObjectType.BOOKMARK);
        activity.setBaseObject(baseObject);

        final List<Long> commentIds = Arrays.asList(1L, 2L);

        CommentDTO comment1 = new CommentDTO();
        comment1.setBody("something something");

        CommentDTO comment2 = new CommentDTO();
        comment2.setBody("another comment");

        final List<CommentDTO> comments = Arrays.asList(comment1, comment2);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(commentIdsByActivityIdDAO).execute(activity.getId());
                will(returnValue(commentIds));

                oneOf(commentsByIdDAO).execute(with(any(List.class)));
                will(returnValue(comments));
            }
        });

        assertEquals(description + " " + comment1.getBody() + " " + comment2.getBody(), sut.objectToString(activity)
                .trim());
    }
}
