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
package org.eurekastreams.web.client.ui.common.stream.renderers.object;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.common.stream.transformers.HashtagLinkTransformer;
import org.eurekastreams.web.client.ui.common.stream.transformers.HyperlinkTransformer;
import org.eurekastreams.web.client.ui.common.stream.transformers.StreamSearchLinkBuilder;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a note.
 * 
 */
public class NoteRenderer implements ObjectRenderer
{
    /**
     * JSNI Facade.
     */
    private WidgetJSNIFacadeImpl jSNIFacade = new WidgetJSNIFacadeImpl();
    
    /**
     * Renders the attachment.
     * 
     * @param activity
     *            the activity.
     * @return the attachment.
     */
    public Widget getAttachmentWidget(final ActivityDTO activity)
    {
        return null;
    }

    /**
     * Renders the content widget.
     * 
     * @param activity
     *            the activity.
     * @return the widget.
     */
    public Widget getContentWidget(final ActivityDTO activity)
    {
        String activityContent = activity.getBaseObjectProperties().get("content").replace("%EUREKA:ACTORNAME%",
                activity.getActor().getDisplayName());

        if (activityContent == null)
        {
            activityContent = "";
        }
        
        // Strip out any existing HTML.
        activityContent = jSNIFacade.escapeHtml(activityContent);
        activityContent = activityContent.replaceAll("(\r\n|\n|\r)", "<br />");
        
        // first transform links to hyperlinks
        String html = new HyperlinkTransformer(new WidgetJSNIFacadeImpl()).transform(activityContent);

        // then transform hashtags to hyperlinks
        html = new HashtagLinkTransformer(new StreamSearchLinkBuilder()).transform(html);

        HTML widget = new HTML(html);
        widget.addStyleName("message-body");

        return widget;
    }

}
