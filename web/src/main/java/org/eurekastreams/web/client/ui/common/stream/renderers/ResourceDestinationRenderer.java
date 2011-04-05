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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import java.util.HashMap;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders the "commented..." message for a post to a resource.
 */
public class ResourceDestinationRenderer implements StatefulRenderer
{
    /** The activity. */
    private final ActivityDTO activity;

    /**
     * Constructor.
     *
     * @param inActivity
     *            The activity.
     */
    public ResourceDestinationRenderer(final ActivityDTO inActivity)
    {
        activity = inActivity;
    }

    /**
     * {@inheritDoc}
     */
    public Widget render()
    {
        HashMap<String, String> props = activity.getBaseObjectProperties();
        String resourceUrl = props.get("resourceUrl");
        String resourceTitle = props.get("resourceTitle");
        String siteUrl = props.get("siteUrl");
        String siteTitle = props.get("siteTitle");

        Panel main = new FlowPanel();
        main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().inlinePanel());

        main.add(new InlineLabel("commented on"));
        main.add(new Anchor(resourceTitle, resourceUrl));
        if (resourceTitle != null && !resourceTitle.isEmpty())
        {
            if (siteUrl != null && !siteUrl.isEmpty())
            {
                main.add(new InlineLabel("from"));
                main.add(new Anchor(siteTitle, siteUrl));
            }
            else
            {
                main.add(new InlineLabel("from " + siteTitle));
            }
        }

        return main;
    }

}
