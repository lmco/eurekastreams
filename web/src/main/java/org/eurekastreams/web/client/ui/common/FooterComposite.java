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
package org.eurekastreams.web.client.ui.common;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * This class creates the Composite for the main footer displayed on the page.
 * 
 */
public class FooterComposite extends Composite
{

    /**
     * The Site Labeling Panel.
     */
    FlowPanel siteLabeling = new FlowPanel();

    /**
     * Primary constructor for the FooterComposite Widget.
     */
    public FooterComposite()
    {
        FlowPanel panel = new FlowPanel();

        FlowPanel navPanel = new FlowPanel();

        Anchor poweredBy = new Anchor("", "http://www.eurekastreams.org", "_blank");
        poweredBy.addStyleName("powered-by-eureka");
        navPanel.add(poweredBy);

        panel.add(navPanel);
        panel.add(siteLabeling);

        navPanel.addStyleName("footer-nav");
        siteLabeling.addStyleName("site-labeling");
        panel.addStyleName("footer-bar");
        initWidget(panel);
    }

    /**
     * Sets Site labeling.
     * 
     * @param siteLabelingTxt
     *            The text for Site Labeling.
     */
    public void setSiteLabel(final String siteLabelingTxt)
    {
        siteLabeling.getElement().setInnerHTML(siteLabelingTxt);
    }
}
