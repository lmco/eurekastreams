/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays statistics about this group. NOTE: these statistics are currenly out of scope and are just hard-coded.
 */
public class GroupStatisticsPanel extends FlowPanel
{
    /**
     * Constructor.
     * 
     * @param inGroup
     *            the group being described.
     */
    public GroupStatisticsPanel(final DomainGroup inGroup)
    {
        Label leadership = new Label("Statistics");
        leadership.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSubheader());
        this.add(leadership);

        // TODO: replace with meaningful statistics
        setupStat("7", "Updates:");
        setupStat("78", "Comments:");
        setupStat("789", "Likes:");
    }

    /**
     * Utility method to set up a label/value pair.
     * 
     * @param value
     *            the statistic value to display
     * @param label
     *            the text label for the statistic
     */
    private void setupStat(final String value, final String label)
    {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileStatistics());
        
        panel.add(new Label(label));
        panel.add(new Hyperlink(value, ""));
        
        this.add(panel);
    }
}
