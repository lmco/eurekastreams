/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.notifier;

import org.eurekastreams.server.support.email.EmailTemplate;

/**
 * Specifies information about a email notification to be built.
 */
public class EmailNotificationTemplate extends EmailTemplate
{
    /** What kind of address to use for the reply-to. */
    public static enum ReplyAction
    {
        /** Don't use a reply address. */
        NONE,

        /** Reply to the actor. */
        ACTOR,

        /** System addres with token to allow commenting. */
        COMMENT
    }

    /** What kind of address to use for the reply-to. */
    private ReplyAction replyAddressType = ReplyAction.NONE;

    // ---- short setters for use by Spring p-namespace ----

    /**
     * @param inReplyAddressType
     *            the replyAddressType to set
     */
    public void setReply(final ReplyAction inReplyAddressType)
    {
        replyAddressType = inReplyAddressType;
    }

    // ---- normal getters and setters ----

    /**
     * @return the replyAddressType
     */
    public ReplyAction getReplyAddressType()
    {
        return replyAddressType;
    }

    /**
     * @param inReplyAddressType
     *            the replyAddressType to set
     */
    public void setReplyAddressType(final ReplyAction inReplyAddressType)
    {
        replyAddressType = inReplyAddressType;
    }
}
