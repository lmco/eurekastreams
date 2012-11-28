/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.renderers.verb;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.MetadataLinkRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.RenderUtilities;
import org.eurekastreams.web.client.ui.common.stream.renderers.ResourceDestinationRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StatefulRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer.State;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.ObjectRenderer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Render a post verb activity.
 *
 */
public class PostRenderer implements VerbRenderer
{
    /**
     * The object dictionary.
     */
    private Map<BaseObjectType, ObjectRenderer> objectRendererDictionary;
    /**
     * The activity.
     */
    private ActivityDTO activity;
    /**
     * The state.
     */
    private StreamMessageItemRenderer.State state;
    /**
     * Whether or not to show the recipient.
     */
    boolean showRecipient;

    /**
     * Setup.
     *
     * @param inObjectRendererDictionary
     *            object dictionary.
     * @param inActivity
     *            the activity.
     * @param inState
     *            the state of the activity.
     * @param inShowRecipient
     *            the recipient.
     */
    public void setup(final Map<BaseObjectType, ObjectRenderer> inObjectRendererDictionary,
            final ActivityDTO inActivity, final StreamMessageItemRenderer.State inState, final boolean inShowRecipient)
    {
        objectRendererDictionary = inObjectRendererDictionary;
        activity = inActivity;
        state = inState;
        showRecipient = inShowRecipient;
    }

    /**
     * Should the verb allow commenting.
     *
     * @return the value.
     */
    public boolean getAllowComment()
    {
        return state.equals(State.DEFAULT);
    }

    /**
     * Should the verb allow sharing.
     *
     * @return the value.
     */
    public boolean getAllowShare()
    {
        if (activity.getBaseObjectType().equals(BaseObjectType.VIDEO))
        {
            return false;
        }

        return state.equals(State.DEFAULT);
    }

    /**
     * Should the verb allow starring.
     *
     * @return the value.
     */
    public boolean getAllowStar()
    {
        return state.equals(State.DEFAULT);
    }

    /**
     * Should the verb allow liking.
     *
     * @return the value.
     */
    public boolean getAllowLike()
    {
        return state.equals(State.DEFAULT);
    }

    /**
     * Get the avatar.
     *
     * @return the avatar.
     */
    public Widget getAvatar()
    {
        return new AvatarRenderer().render(activity.getActor());
    }

    /**
     * Get the content.
     *
     * @return the content.
     */
    public Widget getContent()
    {
        Widget content = objectRendererDictionary.get(activity.getBaseObjectType()).getContentWidget(activity);
        Widget attachment = objectRendererDictionary.get(activity.getBaseObjectType()).getAttachmentWidget(activity);

        FlowPanel contentPanel = new FlowPanel();

        if (content != null)
        {
            contentPanel.add(content);
        }
        if (attachment != null)
        {
            contentPanel.add(attachment);
        }

        return contentPanel;
    }

    /**
     * Return a list of appropriate metadata link renderers.
     *
     * @return the list.
     */
    public List<StatefulRenderer> getMetaDataItemRenderers()
    {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<StatefulRenderer> getSourceMetaDataItemRenderers()
    {
        List<StatefulRenderer> renderers = new LinkedList<StatefulRenderer>();

        RenderUtilities.addActorNameRenderers(renderers, activity);

        if (showRecipient)
        {
            StreamEntityDTO stream = activity.getDestinationStream();
            if (stream.getType() == EntityType.RESOURCE)
            {
                renderers.add(new ResourceDestinationRenderer(activity));
            }
            else
            {
                renderers.add(new MetadataLinkRenderer("to", stream.getType(), stream.getUniqueIdentifier(), stream
                        .getDisplayName()));
            }
        }

        return renderers;
    }
}
