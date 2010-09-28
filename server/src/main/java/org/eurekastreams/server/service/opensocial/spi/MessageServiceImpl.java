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
package org.eurekastreams.server.service.opensocial.spi;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * This is a stub class.
 *
 */
public class MessageServiceImpl implements MessageService
{

    /**
     * Stub Method.
     * @param userId stub.
     * @param appId stub.
     * @param msgCollId stub.
     * @param message stub.
     * @param token stub.
     * @return void on retrieved.
     */
    public Future<Void> createMessage(final UserId userId, final String appId,
            final String msgCollId, final Message message, final SecurityToken token)
    {
        return null;
    }

    /**
     * Stub Method.
     * @param userId stub.
     * @param msgCollection stub.
     * @param token stub.
     * @return MessageCollection on retrieved.
     */
    public Future<MessageCollection> createMessageCollection(final UserId userId,
            final MessageCollection msgCollection, final SecurityToken token)
    {
        return null;
    }

    /**
     * Stub Method.
     * @param userId stub.
     * @param msgCollId stub.
     * @param token stub.
     * @return void on retrieved.
     */
    public Future<Void> deleteMessageCollection(final UserId userId,
            final String msgCollId, final SecurityToken token)
    {
        return null;
    }

    /**
     * Stub Method.
     * @param userId stub.
     * @param msgCollId stub.
     * @param ids stub.
     * @param token stub.
     * @return void on retrieved.
     */
    public Future<Void> deleteMessages(final UserId userId, final String msgCollId,
            final List<String> ids, final SecurityToken token)
    {
        return null;
    }

    /**
     * Stub Method.
     * @param userId stub.
     * @param fields stub.
     * @param options stub.
     * @param token stub.
     * @return RestfulCollection MessageCollection on retrieved.
     */
    public Future<RestfulCollection<MessageCollection>> getMessageCollections(
            final UserId userId, final Set<String> fields, final CollectionOptions options,
            final SecurityToken token)
    {
        return null;
    }

    /**
     * Stub Method.
     * @param userId stub.
     * @param msgCollId stub.
     * @param fields stub.
     * @param msgIds stub.
     * @param options stub.
     * @param token stub.
     * @return RestfulCollection Message retrieved.
     */
    public Future<RestfulCollection<Message>> getMessages(final UserId userId,
            final String msgCollId, final Set<String> fields, final List<String> msgIds,
            final CollectionOptions options, final SecurityToken token)
    {
        return null;
    }

    /**
     * Stub Method.
     * @param userId stub.
     * @param msgCollId stub.
     * @param messageId stub.
     * @param message stub.
     * @param token stub.
     * @return void on retrieved.
     */
    public Future<Void> modifyMessage(final UserId userId, final String msgCollId,
            final String messageId, final Message message, final SecurityToken token)
    {
        return null;
    }

    /**
     * Stub Method.
     * @param userId stub.
     * @param msgCollection stub.
     * @param token stub.
     * @return void on retrieved.
     */
    public Future<Void> modifyMessageCollection(final UserId userId,
            final MessageCollection msgCollection, final SecurityToken token)
    {
        return null;
    }

}
