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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests CommentTemplateEmailBuilder.
 */
public class CommentTemplateEmailBuilderTest
{
    /** Test data. */
    private static final long COMMENT_ID = 4545L;

    /** Test data. */
    private static final String COMMENT_TEXT = "This is the comment";

    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /** Mapper to get the comment. */
    private DomainMapper<List<Long>, List<CommentDTO>> commentsMapper = context.mock(DomainMapper.class);

    /** Fixture: builder. */
    private TemplateEmailBuilder builder = context.mock(TemplateEmailBuilder.class);

    /** Fixture: mail message. */
    private MimeMessage message = context.mock(MimeMessage.class);

    /** Fixture: notification. */
    private NotificationDTO notification;

    /** Fixture: comment. */
    private CommentDTO comment = context.mock(CommentDTO.class);

    /** SUT. */
    private CommentTemplateEmailBuilder sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new CommentTemplateEmailBuilder(commentsMapper, builder);
        notification = new NotificationDTO();
        notification.setCommentId(COMMENT_ID);

        context.checking(new Expectations()
        {
            {
                allowing(comment).getBody();
                will(returnValue(COMMENT_TEXT));
            }
        });
    }

    /**
     * Tests building.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuild() throws Exception
    {
        final AnonymousClassInterceptor<Map<String, String>> parmInt =
                new AnonymousClassInterceptor<Map<String, String>>(1);

        context.checking(new Expectations()
        {
            {
                allowing(commentsMapper).execute(Collections.singletonList(COMMENT_ID));
                will(returnValue(Collections.singletonList(comment)));

                oneOf(builder).build(with(equal(notification)), with(any(Map.class)), with(same(message)));
                will(parmInt);
            }
        });

        sut.build(notification, message);

        context.assertIsSatisfied();
        Map<String, String> parm = parmInt.getObject();
        assertEquals(1, parm.size());
        assertEquals(COMMENT_TEXT, parm.get("comment.content"));
    }

    /**
     * Tests building.
     *
     * @throws Exception
     *             Should.
     */
    @Test(expected = Exception.class)
    public void testBuildNotFound() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(commentsMapper).execute(Collections.singletonList(COMMENT_ID));
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        sut.build(notification, message);

        context.assertIsSatisfied();
    }
}
