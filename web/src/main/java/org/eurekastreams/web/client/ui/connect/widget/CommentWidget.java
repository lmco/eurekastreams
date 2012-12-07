/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.widget;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.common.stream.renderers.MetadataLinkRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.RenderUtilities;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StatefulRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer.State;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.NoteRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.ObjectRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.verb.VerbRenderer;
import org.eurekastreams.web.client.ui.connect.support.PostToStreamComposite;
import org.eurekastreams.web.client.ui.connect.support.StreamPanel;
import org.eurekastreams.web.client.ui.connect.support.WidgetUtilities;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Eureka Connect "comment widget" - displays a stream for a resource and allows posting.
 */
public class CommentWidget extends Composite
{
    /** Unique ID of entity whose stream is displayed. */
    private final String resourceId;

    /** URL of resource whose stream is displayed. */
    private final String resourceUrl;

    /** Description of the resource. */
    private final String resourceTitle;

    /** URL of the site containing the resource. */
    private final String siteUrl;

    /** Description of the site containing the resource. */
    private final String siteTitle;

    /**
     * Constructor.
     *
     * @param inResourceId
     *            Unique ID of resource whose stream to display.
     * @param inResourceUrl
     *            Resource's URL.
     * @param inResourceTitle
     *            Resource's title.
     * @param inSiteUrl
     *            Site's URL (optional).
     * @param inSiteTitle
     *            Site's title (optional).
     */
    public CommentWidget(final String inResourceId, final String inResourceUrl, final String inResourceTitle,
            final String inSiteUrl, final String inSiteTitle)
    {
        resourceId = inResourceId;
        resourceUrl = inResourceUrl;
        resourceTitle = inResourceTitle;
        siteUrl = inSiteUrl;
        siteTitle = inSiteTitle;

        StreamScope streamScope = new StreamScope(ScopeType.RESOURCE, resourceId);

        final StreamPanel streamPanel = new StreamPanel(ShowRecipient.NO,
                new CommentWidgetStreamMessageItemRenderer(),
                new CommentWidgetPostToStreamComposite(streamScope));
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().embeddedWidget());
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectCommentWidget());
        initWidget(streamPanel);

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                // hide everything but the post box if the stream is empty
                // but distinguish between an empty stream and no search results
                boolean emptyStream = Session.getInstance().getParameterValue("search") == null
                        && event.getStream().getPagedSet().isEmpty();
                if (emptyStream)
                {
                    streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyStream());
                }
                else
                {
                    streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyStream());
                }
            }
        });

        String jsonRequest = StreamJsonRequestFactory.addRecipient(EntityType.RESOURCE, resourceId,
                StreamJsonRequestFactory.getEmptyRequest()).toString();

        EventBus.getInstance().notifyObservers(new StreamRequestEvent("", jsonRequest));
        streamPanel.setStreamScope(streamScope, true);
    }

    /**
     * Custom version of the stream item renderer tailored to change the share behavior.
     */
    class CommentWidgetStreamMessageItemRenderer extends StreamMessageItemRenderer
    {
        /**
         * Constructor.
         */
        public CommentWidgetStreamMessageItemRenderer()
        {
            super(ShowRecipient.NO);
            setCreatePermalink(false);
            getObjectDictionary().put(BaseObjectType.BOOKMARK, new NoteRenderer());

            // decorate all the verb renderers
            for (Entry<ActivityVerb, VerbRenderer> entry : new HashMap<ActivityVerb, VerbRenderer>(getVerbDictionary())
                    .entrySet())
            {
                getVerbDictionary().put(entry.getKey(), new CommentWidgetVerbRenderer(entry.getValue()));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onShare(final ActivityDTO activity)
        {
            // display share box in a new pop-up window
            WidgetUtilities.showShareActivityPopup(activity.getId());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void performDelete(final ActivityDTO inMsg)
        {
            // in a resource stream, actually delete the resource item
            ActivityModel.getInstance().delete(inMsg.getId());
        }
    }

    /**
     * Custom version of the post composite tailored to post activities to a resource stream.
     */
    class CommentWidgetPostToStreamComposite extends PostToStreamComposite
    {
        /** Activity populator. */
        private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

        /** Checkbox for whether to post to Eureka (used for resource streams). */
        private final CheckBox postToEurekaCheckBox = new CheckBox("Post to Eureka");

        /** If user's account is locked (presumably a non-ES user). */
        private final boolean accountLocked;

        /**
         * Constructor.
         *
         * @param inScope
         *            Stream scope.
         */
        public CommentWidgetPostToStreamComposite(final StreamScope inScope)
        {
            super(inScope, "Leave a Comment");

            accountLocked = Session.getInstance().getCurrentPerson().isAccountLocked();
            if (!accountLocked)
            {
                postToEurekaCheckBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postToEureka());
                getSubTextboxPanel().insert(postToEurekaCheckBox, 0);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onExpand()
        {
            super.onExpand();
            postToEurekaCheckBox.setValue(!accountLocked);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void postMessage()
        {
            ActivityDTO activity = activityPopulator.getActivityDTO(getMesssageText(), EntityType.RESOURCE,
                    resourceId, new PostPopulator(), new NotePopulator());

            activity.setShowInStream(postToEurekaCheckBox.getValue());
            HashMap<String, String> props = activity.getBaseObjectProperties();
            props.put("resourceUrl", resourceUrl);
            props.put("resourceTitle", resourceTitle);
            props.put("siteUrl", siteUrl);
            props.put("siteTitle", siteTitle);

            PostActivityRequest postRequest = new PostActivityRequest(activity);
            ActivityModel.getInstance().insert(postRequest);
        }
    }

    /**
     * Decorator for verb renderers to display the actor line on bookmark activities as "xyz shared this link to zyx".
     */
    class CommentWidgetVerbRenderer implements VerbRenderer
    {
        /** Decorated renderer. */
        private final VerbRenderer decorated;

        /** Activity being rendered. */
        private ActivityDTO activity;

        /**
         * Constructor.
         *
         * @param inDecorated
         *            Decorated renderer.
         */
        public CommentWidgetVerbRenderer(final VerbRenderer inDecorated)
        {
            decorated = inDecorated;
        }

        /**
         * {@inheritDoc}
         */
        public List<StatefulRenderer> getSourceMetaDataItemRenderers()
        {
            List<StatefulRenderer> renderers = new LinkedList<StatefulRenderer>();

            RenderUtilities.addActorNameRenderers(renderers, activity);

            if (activity.getBaseObjectType() == BaseObjectType.BOOKMARK
                    && activity.getDestinationStream().getType() != EntityType.RESOURCE)
            {
                StreamEntityDTO stream = activity.getDestinationStream();
                renderers.add(new MetadataLinkRenderer("shared this link to", stream.getType(), stream
                        .getUniqueIdentifier(), stream.getDisplayName()));
            }

            return renderers;
        }

        /**
         * {@inheritDoc}
         */
        public void setup(final Map<BaseObjectType, ObjectRenderer> inObjectRendererDictionary,
                final ActivityDTO inActivity, final State inState, final boolean inShowRecipient)
        {
            activity = inActivity;
            decorated.setup(inObjectRendererDictionary, inActivity, inState, inShowRecipient);
        }

        // boring pass-through methods (decoration)

        /**
         * {@inheritDoc}
         */
        public boolean getAllowComment()
        {
            return decorated.getAllowComment();
        }

        /**
         * {@inheritDoc}
         */
        public boolean getAllowStar()
        {
            return decorated.getAllowStar();
        }

        /**
         * {@inheritDoc}
         */
        public boolean getAllowLike()
        {
            return decorated.getAllowLike();
        }

        /**
         * {@inheritDoc}
         */
        public boolean getAllowShare()
        {
            return decorated.getAllowShare();
        }

        /**
         * {@inheritDoc}
         */
        public Widget getAvatar()
        {
            return decorated.getAvatar();
        }

        /**
         * {@inheritDoc}
         */
        public Widget getContent()
        {
            return decorated.getContent();
        }

        /**
         * {@inheritDoc}
         */
        public List<StatefulRenderer> getMetaDataItemRenderers()
        {
            return decorated.getMetaDataItemRenderers();
        }

    }
}
