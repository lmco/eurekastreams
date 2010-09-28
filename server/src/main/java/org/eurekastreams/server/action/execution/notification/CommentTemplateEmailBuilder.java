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

import java.util.Collections;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Notification email builder specifically for emails regarding comments which contain the text of the comment. Looks up
 * the comment then invokes a standard template builder to do the work. Really all this does is allow extra data to be
 * available to the template.
 */
public class CommentTemplateEmailBuilder implements NotificationEmailBuilder
{
    /** Mapper to get the comment. */
    private DomainMapper<List<Long>, List<CommentDTO>> commentsMapper;

    /** Builder to use. */
    private TemplateEmailBuilder builder;

    /**
     * Constructor.
     *
     * @param inCommentsMapper
     *            Mapper to get the comment.
     * @param inBuilder
     *            Builder to use.
     */
    public CommentTemplateEmailBuilder(final DomainMapper<List<Long>, List<CommentDTO>> inCommentsMapper,
            final TemplateEmailBuilder inBuilder)
    {
        commentsMapper = inCommentsMapper;
        builder = inBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(final NotificationDTO inNotification, final MimeMessage inMessage) throws Exception
    {
        // get activity ID from comment
        List<CommentDTO> commentList =
                commentsMapper.execute(Collections.singletonList(inNotification.getCommentId()));
        if (commentList.isEmpty())
        {
            throw new Exception("Cannot retrieve comment " + inNotification.getCommentId());
        }

        // invoke builder with comment text
        builder.build(inNotification, Collections.singletonMap("comment.content", commentList.get(0).getBody()),
                inMessage);
    }
}
