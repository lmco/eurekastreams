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

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GroupModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Help content page.
 */
public class HelpContent extends Composite
{
    /**
     * Main panel.
     */
    private final FlowPanel panel;

    /**
     * Event handler for group loading.
     */
    private class GotGroupResponseEvent implements Observer<GotGroupModelViewInformationResponseEvent>
    {
        /**
         * The already-loaded system settings.
         */
        private final SystemSettings systemSettings;

        /**
         * Constructor.
         * 
         * @param inSystemSettings
         *            the already-loaded system settings.
         */
        public GotGroupResponseEvent(final SystemSettings inSystemSettings)
        {
            systemSettings = inSystemSettings;
        }

        /**
         * Event handler for when the group is loaded.
         * 
         * @param supportGroupEvent
         *            group-loaded event
         */
        public void update(final GotGroupModelViewInformationResponseEvent supportGroupEvent)
        {
            // make sure the group that was just loaded is the support group
            DomainGroupModelView group = supportGroupEvent.getResponse();
            if (group.getShortName().equals(systemSettings.getSupportStreamGroupShortName()))
            {
                // this is the group we're looking for, setup the form
                Session.getInstance().getEventBus().removeObserver(GotGroupModelViewInformationResponseEvent.class,
                        this);
                buildPage(systemSettings, group);
            }
        }
    }

    /**
     * Default constructor.
     */
    public HelpContent()
    {
        panel = new FlowPanel();
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().help());

        // get the system settings, asynchronously
        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        // got the system settings - remove the observer
                        Session.getInstance().getEventBus().removeObserver(GotSystemSettingsResponseEvent.class, this);
                        final SystemSettings settings = event.getResponse();

                        if (settings.getSupportStreamGroupShortName() != null
                                && settings.getSupportStreamGroupShortName().length() > 0)
                        {
                            // the support group is set, go get it
                            Session.getInstance().getEventBus().addObserver(
                                    GotGroupModelViewInformationResponseEvent.class,
                                    new GotGroupResponseEvent(settings));

                            GroupModel.getInstance().fetch(settings.getSupportStreamGroupShortName(), true);
                        }
                        else
                        {
                            // there's no support group - only show the "Help Documentation" panel, and show it centered
                            buildPage(settings, null);
                        }
                    }
                });

        SystemSettingsModel.getInstance().fetch(null, true);
        initWidget(panel);
    }

    /**
     * Build the page.
     * 
     * @param settings
     *            the SystemSettings
     * @param supportGroup
     *            the support Domain Group
     */
    private void buildPage(final SystemSettings settings, final DomainGroupModelView supportGroup)
    {
        FlowPanel leftPanel = new FlowPanel();
        leftPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().leftPanel());

        if (supportGroup != null)
        {
            // there's a support group - add the "Support Stream" panel
            SupportStreamHelpPanel supportStreamHelpPanel = new SupportStreamHelpPanel(settings, supportGroup);
            supportStreamHelpPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().supportStreamHelpPanel());
            leftPanel.add(supportStreamHelpPanel);
        }

        String supportPhoneNumber = settings.getSupportPhoneNumber();
        String supportEmailAddress = settings.getSupportEmailAddress();

        if ((supportPhoneNumber != null && supportPhoneNumber.length() > 0)
                || (supportEmailAddress != null && supportEmailAddress.length() > 0))
        {
            // we have either the support email address or phone number - show the contact panel
            SupportContactHelpPanel supportContactHelpPanel = new SupportContactHelpPanel(supportPhoneNumber,
                    supportEmailAddress);
            supportContactHelpPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().supportStreamContactPanel());
            leftPanel.add(supportContactHelpPanel);
        }

        DocumentationHelpPanel documentationHelpPanel = new DocumentationHelpPanel();
        documentationHelpPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().helpDocumentationPanel());

        FlowPanel documentationWrapperPanel = new FlowPanel();
        documentationWrapperPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().helpDocumentationWrapper());
        documentationWrapperPanel.add(documentationHelpPanel);

        FlowPanel rightPanel = new FlowPanel();
        rightPanel.add(documentationWrapperPanel);

        String version = new WidgetJSNIFacadeImpl().getWindowValue("appVersion");
        Label versionLabel = new Label("Eureka Streams version " + version);
        versionLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().helpAppVersion());
        rightPanel.add(versionLabel);

        if (leftPanel.getWidgetCount() > 0)
        {
            // we have widgets in the left panel, so show it
            leftPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().leftPanel());
            panel.add(leftPanel);

            rightPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().rightPanel());
        }
        else
        {
            // no widgets in the left panel - show the right panel only, and centered
            rightPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().centerPanel());
        }
        panel.add(rightPanel);
    }
}
