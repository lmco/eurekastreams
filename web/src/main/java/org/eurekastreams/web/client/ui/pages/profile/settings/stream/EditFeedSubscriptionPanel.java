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
package org.eurekastreams.web.client.ui.pages.profile.settings.stream;

import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * The display for the add and edit feed subscriptions panel.
 *
 */
public class EditFeedSubscriptionPanel extends FlowPanel
{

    /**
     * The metadata of the selected plugin.
     */
    GadgetMetaDataDTO metaData;

    /**
     * Default Constructor.
     *
     * @param inMetaData
     *             The metadata of the selected plugin
     * @param mode
     *             The mode of the panel
     */
    public EditFeedSubscriptionPanel(final GadgetMetaDataDTO inMetaData, final Method mode)
    {
        metaData = inMetaData;

        this.addStyleName("stream-plugins-feed-subscriptions-container");

        Label feedSubscriptionsHeader = new Label();

        if (mode == Method.INSERT)
        {
            feedSubscriptionsHeader.setText("Add a New Plugin");
            this.addStyleName("stream-plugins-feed-subscriptions-container-add");
        }
        else if (mode == Method.UPDATE)
        {
            feedSubscriptionsHeader.setText("Edit Plugin");
            this.addStyleName("stream-plugins-feed-subscriptions-container-edit");
        }

        feedSubscriptionsHeader.addStyleName("header");
        this.add(feedSubscriptionsHeader);


    }


}
