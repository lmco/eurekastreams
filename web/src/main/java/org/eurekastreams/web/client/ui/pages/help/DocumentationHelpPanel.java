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
package org.eurekastreams.web.client.ui.pages.help;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * Help documentation panel.
 */
public class DocumentationHelpPanel extends FlowPanel
{

    /**
     * Get the eureka streams url.
     * 
     * @return the eureka streams url
     */
    private static native String getEurekaStreamsUrl() /*-{
                    return $wnd.eurekastreams.container.getExternalUrl();
                }-*/;

    /**
     * Constructor.
     */
    public DocumentationHelpPanel()
    {
        this.add(buildHeaderPanel());
        add(buildDescriptionPanel());

        FlowPanel videoPanel = new FlowPanel();
        videoPanel.addStyleName("documentation-bullet-block");
        videoPanel.add(buildTourPanel());
        videoPanel.add(buildGettingStartedPanel());
        videoPanel.add(buildFaqsPanel());
        add(videoPanel);

        add(buildGotoEurekaStreamsButton());
    }

    /**
     * Build and return the header panel.
     * 
     * @return the header panel.
     */
    private FlowPanel buildHeaderPanel()
    {
        FlowPanel headerPanel = new FlowPanel();
        headerPanel.addStyleName("header");

        Label headerLabel = new Label("Help Documentation");
        headerPanel.add(headerLabel);
        return headerPanel;
    }

    /**
     * Build and return the panel showing the description for the section.
     * 
     * @return the panel showing the description for the section
     */
    private FlowPanel buildDescriptionPanel()
    {
        Label descLabel1 = new Label("A variety of help and support documentation can be found on the ");
        Anchor anchor = new Anchor("Eureka Streams", getEurekaStreamsUrl(), "_blank");
        Label descLabel2 = new Label("website.");

        descLabel1.addStyleName("header-description-component");
        anchor.addStyleName("header-description-component");
        descLabel2.addStyleName("header-description-component");

        FlowPanel panel = new FlowPanel();
        panel.add(descLabel1);
        panel.add(anchor);
        panel.add(descLabel2);

        panel.addStyleName("documentation-bullet");
        return panel;
    }

    /**
     * Build and return the Tour panel.
     * 
     * @return the Tour panel.
     */
    private FlowPanel buildTourPanel()
    {
        FlowPanel panel = new FlowPanel();
        Anchor imageBlockPanel = new Anchor("", "http://eurekastreams.org/tours/reputation", "_blank");
        imageBlockPanel.addStyleName("getting-started-help-image-box");

        FlowPanel textPanel = new FlowPanel();
        textPanel.setStyleName("help-documentation-text-block");

        panel.add(imageBlockPanel);
        panel.add(textPanel);

        Anchor header = new Anchor("Tour", "http://eurekastreams.org/tours/reputation", "_blank");
        header.addStyleName("display-name");
        Label content = new Label("Learn how Eureka Streams can help you: build your reputation and career, "
                + "enhance your team’s or community’s communication, and create your own personalized experience.");

        textPanel.add(header);
        textPanel.add(content);

        panel.addStyleName("documentation-bullet");
        return panel;
    }

    /**
     * Build and return the Getting Started Videos panel.
     * 
     * @return the Getting started Videos panel
     */
    private FlowPanel buildGettingStartedPanel()
    {
        FlowPanel panel = new FlowPanel();
        Anchor imageBlockPanel = new Anchor("", "http://eurekastreams.org/getting-started", "_blank");
        imageBlockPanel.addStyleName("instructional-videos-help-image-box");

        FlowPanel textPanel = new FlowPanel();
        textPanel.setStyleName("help-documentation-text-block");

        panel.add(imageBlockPanel);
        panel.add(textPanel);

        Anchor header = new Anchor("Getting Started Videos", "http://eurekastreams.org/getting-started", "_blank");
        header.addStyleName("display-name");
        Label content = new Label("View short video overviews showing you how to get "
                + "started with of the primary features and functions of Eureka Streams");

        textPanel.add(header);
        textPanel.add(content);

        panel.addStyleName("documentation-bullet");
        return panel;
    }

    /**
     * Build and return the FAQs panel.
     * 
     * @return the FAQs panel
     */
    private FlowPanel buildFaqsPanel()
    {
        FlowPanel panel = new FlowPanel();
        Anchor imageBlockPanel = new Anchor("", "http://eurekastreams.org/faqs", "_blank");
        imageBlockPanel.addStyleName("faqs-help-image-box ");

        FlowPanel textPanel = new FlowPanel();
        textPanel.setStyleName("help-documentation-text-block");

        panel.add(imageBlockPanel);
        panel.add(textPanel);

        Anchor header = new Anchor("FAQs", "http://eurekastreams.org/faqs", "_blank");
        header.addStyleName("display-name");
        Label content = new Label("Get answers to some of the most frequently asked questions about Eureka Streams.");

        textPanel.add(header);
        textPanel.add(content);

        panel.addStyleName("documentation-bullet");
        return panel;
    }

    /**
     * Build and return the "Go to EurekaStreams.org" button.
     * 
     * @return the "Go to EurekaStreams.org" button
     */
    private FlowPanel buildGotoEurekaStreamsButton()
    {
        FlowPanel panel = new FlowPanel();

        HTML goToContainer = new HTML("<a href='http://eurekastreams.org' target='_blank'></a>");
        goToContainer.addStyleName("go-to-eureka-streams-image-label");

        panel.add(goToContainer);

        return panel;
    }

}
