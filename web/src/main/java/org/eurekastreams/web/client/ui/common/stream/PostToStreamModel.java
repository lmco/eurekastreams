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
package org.eurekastreams.web.client.ui.common.stream;

import java.util.HashMap;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageTextAreaChangedEvent;
import org.eurekastreams.web.client.events.errors.ErrorPostingMessageToNullScopeEvent;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Post to stream model.
 */
public class PostToStreamModel
{
    /**
     * Maximum length of a message.
     */
    private static final int MAX_MESSAGE_LENGTH = 250;

    /**
     * The message.
     */
    private String message = "";

    /**
     * The action processor.
     */
    private ActionProcessor processor;

    /**
     * The stream scope.
     */
    // private StreamScope scope;
    private PostToPanel postToPanel;

    /**
     * The recipient type.
     */
    private EntityType recipientType;

    /**
     * The link information.
     */
    private Attachment attachment;

    /**
     * Action keys associated with recipient types.
     */
    private HashMap<EntityType, String> actionKeys = new HashMap<EntityType, String>();

    /**
     * The event bus.
     */
    private EventBus eventBus;

    /**
     * Activity Populator.
     */
    private ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

    /**
     * Constructor.
     *
     * @param inEventBus
     va:2: Line doe*            the event bus.
     * @param inProcessor
     *            the action processor.
     * @param inPostToPanel
     *            the scope.
     */
    public PostToStreamModel(final EventBus inEventBus,
            final ActionProcessor inProcessor, final PostToPanel inPostToPanel)
    {
        eventBus = inEventBus;
        processor = inProcessor;
        postToPanel = inPostToPanel;

    }

    /**
     * Set the message.
     *
     * @param inMessage
     *            the message.
     */
    public void setMessage(final String inMessage)
    {
        message = inMessage;
        eventBus.notifyObservers(MessageTextAreaChangedEvent.getEvent());
    }

    /**
     * @return the number of remaining characters.
     */
    public int getRemainingMessageCharacters()
    {
        return MAX_MESSAGE_LENGTH - message.length();
    }

    /**
     * @return if a message can be posted.
     */
    public boolean isMessageLengthAcceptable()
    {
        return (message.length() <= MAX_MESSAGE_LENGTH && message.length() != 0);
    }

    /**
     * @return gets the message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Posts the message.
     */
    public void postMessage()
    {
        StreamScope scope = postToPanel.getPostScope();

        if (scope != null)
        {
            if (scope.getScopeType().equals(ScopeType.PERSON))
            {
                recipientType = EntityType.PERSON;
            }
            else
            {
                recipientType = EntityType.GROUP;
            }

            actionKeys.put(EntityType.GROUP, "postGroupActivityServiceActionTaskHandler");
            actionKeys.put(EntityType.PERSON, "postPersonActivityServiceActionTaskHandler");

            ActivityDTOPopulatorStrategy objectStrat = new NotePopulator();

            if (attachment != null)
            {
                objectStrat = attachment.getPopulator();
            }

            PostActivityRequest postRequest = new PostActivityRequest(
                    activityPopulator
                    .getActivityDTO(message, recipientType, scope
                            .getUniqueKey(), new PostPopulator(), objectStrat));

            processor.makeRequest(new ActionRequestImpl<Integer>(actionKeys
                    .get(recipientType), postRequest),
                    new AsyncCallback<ActivityDTO>()
                    {
                        /* implement the async call back methods */
                        public void onFailure(final Throwable caught)
                        {
                            // TODO handle error.
                        }

                        public void onSuccess(final ActivityDTO result)
                        {
                            MessageStreamAppendEvent msgEvent = new MessageStreamAppendEvent(
                                    result);
                            eventBus.notifyObservers(msgEvent);
                        }
                    });
        }
        else
        {
            ErrorPostingMessageToNullScopeEvent error = new ErrorPostingMessageToNullScopeEvent();
            error
                    .setErrorMsg("The stream name you entered could not be found");
            eventBus.notifyObservers(error);
        }
    }

    /**
     * Set the attached link.
     *
     * @param inAttachment
     *            the link
     */
    public void setAttachment(final Attachment inAttachment)
    {
        attachment = inAttachment;
    }

    /**
     * Get the attached link.
     *
     * @return the link.
     */
    public Attachment getAttachment()
    {
        return attachment;
    }



}
