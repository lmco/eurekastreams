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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Comment Added event.
 * 
 */
public class CommentAddedEvent
{
    /**
     * Comment DTO.
     */
    private CommentDTO comment;
    /**
     * Message Id.
     */
    private Long messageId;

    /**
     * Default constructor.
     * 
     * @param inComment
     *            the comment.
     * @param inMessageId
     *            the message id.
     */
    public CommentAddedEvent(final CommentDTO inComment, final Long inMessageId)
    {
        comment = inComment;
        messageId = inMessageId;
    }

    /**
     * Gets the message id.
     * 
     * @return the message id.
     */
    public Long getMessageId()
    {
        return messageId;
    }

    /**
     * Gets the comment.
     * 
     * @return the comment.
     */
    public CommentDTO getComment()
    {
        return comment;
    }
}
