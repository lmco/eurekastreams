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
package org.eurekastreams.web.client.ui.common.stream.renderers.object;

import org.eurekastreams.server.domain.stream.ActivityDTO;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a file (document) object.
 * 
 */
public class FileRenderer implements ObjectRenderer
{

    /**
     * {@inheritDoc}
     */
    public Widget getAttachmentWidget(final ActivityDTO activity)
    {
        String title = activity.getBaseObjectProperties().get("targetTitle");
        String url = activity.getBaseObjectProperties().get("targetUrl");

        String ext = url.substring(url.lastIndexOf("."));
        String author = activity.getOriginalActor().getDisplayName();

        FlowPanel file = new FlowPanel();
        file.add(new Label(url));

        file.addStyleName("icon icon-" + ext);

        return file;
    }

    /**
     * {@inheritDoc}
     */
    public Widget getContentWidget(final ActivityDTO activity)
    {
        String title = activity.getBaseObjectProperties().get("targetTitle");
        String url = activity.getBaseObjectProperties().get("targetUrl");

        FlowPanel file = new FlowPanel();

        int extIndex = url.lastIndexOf(".");

        // Make sure there is an extension
        if (extIndex > 0 && extIndex + 2 < url.length())
        {
            String ext = url.substring(extIndex + 1);
            file.addStyleName("icon icon-" + ext);
        }

        String author = activity.getOriginalActor().getDisplayName();

        file.add(new Label(url));

        return file;
    }

}
