/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogFactory;
import org.eurekastreams.web.client.ui.common.notification.NotificationCountWidget;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.search.GlobalSearchComposite;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * HeaderComposite draws the header bar for the user.
 * 
 */
public class HeaderComposite extends Composite
{
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel startPageLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel externalPageLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel activityLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel settingsLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel directoryLinkPanel = new FlowPanel();
    /**
     * Link Panel to encapsulate external links in header.
     */
    FlowPanel galleryLinkPanel = new FlowPanel();
    /**
     * The Site Labing panel.
     */
    FlowPanel siteLabelingContainer = new FlowPanel();

    /** The search box. */
    private final GlobalSearchComposite profileSearchBox = new GlobalSearchComposite("search profiles");

    /**
     * Notification Count widget.
     */
    private final NotificationCountWidget notif = new NotificationCountWidget();

    /**
     * The link map.
     */
    private final HashMap<Page, Hyperlink> linkMap = new HashMap<Page, Hyperlink>();

    /**
     * Primary constructor for the Header composite.
     */
    public HeaderComposite()
    {
        Session.getInstance().getEventBus().addObserver(SwitchedHistoryViewEvent.class,
                new Observer<SwitchedHistoryViewEvent>()
                {
                    public void update(final SwitchedHistoryViewEvent eventArg)
                    {
                        if (eventArg != null)
                        {
                            if (eventArg.getPage() != null)
                            {
                                setActive(eventArg.getPage());
                            }
                        }
                    }
                }, true);
    }

    /**
     * Render the header.
     * 
     * @param viewer
     *            - user to display.
     */
    public void render(final PersonModelView viewer)
    {
        HorizontalULPanel userNav;
        FlowPanel panel = new FlowPanel();
        FlowPanel navPanel = new FlowPanel();

        Anchor externalLink = new Anchor("Eureka Streams", "http://www.eurekastreams.org", "_blank");
        externalLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());

        Hyperlink startPageLink = new Hyperlink("Start Page", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.START)));
        startPageLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        Hyperlink activityLink = new Hyperlink("Activity", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ACTIVITY)));
        activityLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        Hyperlink directoryLink = new Hyperlink("Profiles", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ORGANIZATIONS)));
        directoryLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());

        Hyperlink settingsLink = new Hyperlink("Settings", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.SETTINGS)));
        settingsLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        Hyperlink myProfileLink = new Hyperlink("My Profile", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.PEOPLE, viewer.getAccountId())));
        myProfileLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());
        Hyperlink helpLink = new Hyperlink("Help", Session.getInstance().generateUrl(new CreateUrlRequest(Page.HELP)));
        helpLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBarButton());

        externalPageLinkPanel.add(externalLink);
        externalPageLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().externalHeaderButton());
        startPageLinkPanel.add(startPageLink);
        startPageLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().startHeaderButton());
        activityLinkPanel.add(activityLink);
        activityLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().activityHeaderButton());
        directoryLinkPanel.add(directoryLink);
        directoryLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().directoryHeaderButton());
        settingsLinkPanel.add(settingsLink);
        settingsLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().settingsHeaderButton());

        // galleryLinkPanel.add(galleryLink);

        linkMap.put(Page.START, startPageLink);
        linkMap.put(Page.ACTIVITY, activityLink);
        linkMap.put(Page.ORGANIZATIONS, directoryLink);
        linkMap.put(Page.GROUPS, directoryLink);
        linkMap.put(Page.PEOPLE, directoryLink);
        linkMap.put(Page.GROUP_SETTINGS, directoryLink);
        linkMap.put(Page.PERSONAL_SETTINGS, directoryLink);
        linkMap.put(Page.SETTINGS, settingsLink);
        linkMap.put(Page.HELP, helpLink);
        // linkMap.put(HeaderLink.GALLERY, galleryLinkPanel);

        HorizontalULPanel mainNav = new HorizontalULPanel();

        userNav = new HorizontalULPanel();
        userNav.setShowBars(false);

        mainNav.add(externalPageLinkPanel);

        if (null == viewer) // The user is NOT logged in.
        {
            userNav.add(new Hyperlink("Help", Session.getInstance().generateUrl(new CreateUrlRequest(Page.HELP))));

            final Dialog loginDialog = DialogFactory.getDialog("login");

            Hyperlink loginLink = new Hyperlink();
            loginLink.setText("Login");
            loginLink.addClickListener(new ClickListener()
            {
                public void onClick(final Widget arg0)
                {
                    loginDialog.show();
                }
            });
            loginLink.setTargetHistoryToken(History.getToken());

            userNav.add(loginLink);
        }
        else
        {
            // The user IS logged in
            mainNav.add(startPageLinkPanel);
            mainNav.add(activityLinkPanel);
            mainNav.add(directoryLinkPanel);
            mainNav.add(galleryLinkPanel);
            notif.init();
            userNav.add(notif, "notif-count-list-item");

            FlowPanel myProfileLinkPanel = new FlowPanel();
            myProfileLinkPanel.add(myProfileLink);
            myProfileLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().myProfileHeaderButton());
            userNav.add(myProfileLinkPanel);

            userNav.add(settingsLinkPanel);

            FlowPanel helpLinkPanel = new FlowPanel();
            helpLinkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().helpHeaderButton());
            helpLinkPanel.add(helpLink);
            userNav.add(helpLinkPanel);

            if (Session.getInstance().getAuthenticationType() == AuthenticationType.FORM)
            {
                userNav.add(new HTML("<a href='/j_spring_security_logout'>Logout</a>"));
            }

            // Note: The profile search box is created at constructor time because it registers listeners on the event
            // bus which needs to happen before the call to bufferObservers. The profile search box is created only once
            // (not replaced on page changes), so its listeners must be buffered, else they would be lost on the first
            // page change.
            userNav.add(profileSearchBox);
        }

        // Style the Elements
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().headerBar());
        navPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().navBar());
        siteLabelingContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().siteLabeling());
        mainNav.addStyleName(StaticResourceBundle.INSTANCE.coreCss().mainNav());
        userNav.addStyleName(StaticResourceBundle.INSTANCE.coreCss().userBar());

        // Add the elements to the main panel
        navPanel.add(mainNav);
        navPanel.add(userNav);
        panel.add(navPanel);

        panel.add(siteLabelingContainer);

        initWidget(panel);
        setActive(Session.getInstance().getUrlPage());
    }

    /**
     * Sets Site labeling.
     * 
     * @param inTemplate
     *            HTML template content to insert in the footer.
     * @param inSiteLabel
     *            The text for Site Labeling.
     */
    public void setSiteLabelTemplate(final String inTemplate, final String inSiteLabel)
    {
        String siteLabel = inSiteLabel == null ? "" : inSiteLabel;
        String template = inTemplate.replace("%SITELABEL%", siteLabel);
        siteLabelingContainer.getElement().setInnerHTML(template);
    }

    /**
     * Set the top button as active.
     * 
     * @param page
     *            the page to activate.
     */
    public void setActive(final Page page)
    {
        for (Page specificPage : linkMap.keySet())
        {
            linkMap.get(specificPage).removeStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }

        if (linkMap.containsKey(page))
        {
            linkMap.get(page).addStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }
    }
}
