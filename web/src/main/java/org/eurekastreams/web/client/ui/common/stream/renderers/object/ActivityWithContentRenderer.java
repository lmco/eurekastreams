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
package org.eurekastreams.web.client.ui.common.stream.renderers.object;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.web.client.ui.common.stream.renderers.content.ContentParser;
import org.eurekastreams.web.client.ui.common.stream.renderers.content.ContentSegment;
import org.eurekastreams.web.client.ui.common.stream.renderers.content.ParsedContentRenderer;
import org.eurekastreams.web.client.ui.common.stream.transformers.ActivityStreamSearchLinkBuilder;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content body rendering for activities (note, bookmark, video).
 */
public abstract class ActivityWithContentRenderer implements ObjectRenderer
{
    /** Parses the content. */
    private static ContentParser parser = new ContentParser();

    /** Renders the parsed content. */
    private static ParsedContentRenderer renderer = new ParsedContentRenderer();

    /**
     * Renders the attachment.
     *
     * @param activity
     *            the activity.
     * @return the attachment.
     */
    public abstract Widget getAttachmentWidget(final ActivityDTO activity);

    /**
     * Renders the content widget.
     *
     * @param activity
     *            the activity.
     * @return the widget.
     */
    public Widget getContentWidget(final ActivityDTO activity)
    {
        String activityContent = activity.getBaseObjectProperties().get("content");
        if (activityContent == null)
        {
            return null;
        }

        StreamEntityDTO actor = activity.getOriginalActor() != null ? activity.getOriginalActor() : activity
                .getActor();
        final String actorDisplayName = actor != null ? actor.getDisplayName() : "";
        activityContent = activityContent.replace("%EUREKA:ACTORNAME%", actorDisplayName);

        ComplexPanel widget = new FlowPanel();
        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().messageBody());

        ContentSegment segmentList = parser.split(activityContent);
        renderer.renderList(segmentList, widget, new ActivityStreamSearchLinkBuilder(activity));

        return widget;
    }
}
