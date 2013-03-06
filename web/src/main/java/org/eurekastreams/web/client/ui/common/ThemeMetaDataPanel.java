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
package org.eurekastreams.web.client.ui.common;

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.SwitchToFilterOnPagedFilterPanelEvent;
import org.eurekastreams.web.client.events.data.DeletedThemeResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.ThemeModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Displays a theme for the gallery.
 * 
 */
public class ThemeMetaDataPanel extends FlowPanel
{

    /**
     * The theme.
     */
    private Theme theme;
    /**
     * The apply theme button.
     */
    private Label applyTheme;

    /**
     * Delete button.
     */
    private Hyperlink editTheme;

    /**
     * JSNI Facade.
     */
    private WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * The default constructor.
     * 
     * @param inTheme
     *            the theme.
     */
    public ThemeMetaDataPanel(final Theme inTheme)
    {
        theme = inTheme;

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().gadgetMetaData());

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            EditPanel editControls = new EditPanel(this, Mode.EDIT_AND_DELETE);

            final HashMap<String, String> params = new HashMap<String, String>();
            params.put("action", "editTheme");
            params.put("url", theme.getUrl());
            params.put("category", theme.getCategory().toString());
            params.put("id", String.valueOf(theme.getId()));

            // we need to pass in a tab to the edit handler - we'll pluck it out of the url - carefully
            String currentTab = "1"; // default to 1, just in case
            if (Session.getInstance() != null && Session.getInstance().getHistoryHandler() != null
                    && Session.getInstance().getHistoryHandler().getParameterValue("tab") != null)
            {
                currentTab = Session.getInstance().getHistoryHandler().getParameterValue("tab");
            }
            params.put("tab", currentTab);

            final WidgetJSNIFacadeImpl jsni = new WidgetJSNIFacadeImpl();

            editControls.addEditClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    jsni.setHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.GALLERY, params)),
                            true);
                }

            });

            this.add(editControls);

            editControls.addDeleteClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete this theme? "
                            + "Deleting a theme will remove it from the start page and apply the default theme for "
                            + "every user that has applied it."))
                    {
                        ThemeModel.getInstance().delete(theme.getId());

                        Session.getInstance()
                                .getEventBus()
                                .notifyObservers(
                                        new ShowNotificationEvent(new Notification("The " + theme.getName()
                                                + " theme has been deleted.")));
                    }
                }
            });
        }

        FlowPanel dataPanel = new FlowPanel();
        FlowPanel bannerPanel = new FlowPanel();
        bannerPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().banner());
        bannerPanel.add(new Image(theme.getBannerId()));
        Label title = new Label(theme.getName());
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());

        dataPanel.add(bannerPanel);
        dataPanel.add(title);

        dataPanel.add(new Label(theme.getDescription()));
        FlowPanel gadgetExtInfo = new FlowPanel();
        gadgetExtInfo.addStyleName(StaticResourceBundle.INSTANCE.coreCss().gadgetExtInfo());
        gadgetExtInfo.add(new HTML("Category: "));
        Anchor category = new Anchor();
        category.setText(theme.getCategory().getName());

        category.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent arg0)
            {
                EventBus.getInstance().notifyObservers(
                        new SwitchToFilterOnPagedFilterPanelEvent("themes", theme.getCategory().getName(), "Recent"));
            }
        });

        gadgetExtInfo.add(category);
        insertActionSeparator(gadgetExtInfo);
        gadgetExtInfo.add(new HTML(" Users: <span class='light'>" + theme.getNumberOfUsers() + "</span>"));
        insertActionSeparator(gadgetExtInfo);
        gadgetExtInfo.add(new HTML(" Author: <a href='mailto:" + theme.getAuthorEmail() + "'>" + theme.getAuthorName()
                + "</a>"));
        insertActionSeparator(gadgetExtInfo);
        gadgetExtInfo.add(new HTML(" Publish date: <span class='light'>"
                + DateTimeFormat.getLongDateFormat().format(theme.getCreatedDate()) + "</span>"));
        dataPanel.add(gadgetExtInfo);

        applyTheme = new Label("Apply Theme");
        applyTheme.addStyleName(StaticResourceBundle.INSTANCE.coreCss().applyTheme());
        applyTheme.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                ThemeModel.getInstance().set(theme);
            }
        });

        if (!jSNIFacade.getViewer().equals("null"))
        {
            dataPanel.add(applyTheme);
        }

        final ThemeMetaDataPanel thisBuffered = this;

        Session.getInstance().getEventBus()
                .addObserver(DeletedThemeResponseEvent.class, new Observer<DeletedThemeResponseEvent>()
                {
                    public void update(final DeletedThemeResponseEvent arg1)
                    {
                        if (arg1.getResponse() == theme.getId())
                        {
                            thisBuffered.setVisible(false);
                        }
                    }
                });

        this.add(dataPanel);
    }

    /**
     * Adds a separator (dot).
     * 
     * @param panel
     *            Panel to put the separator in.
     */
    private void insertActionSeparator(final Panel panel)
    {
        Label sep = new InlineLabel("\u2219");
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }

    /**
     * Gets the theme.
     * 
     * @return the theme.
     */
    public Theme getTheme()
    {
        return theme;
    }

    /**
     * Sets the theme as active or not.
     * 
     * @param active
     *            value.
     */
    public void setActive(final Boolean active)
    {
        if (active)
        {
            applyTheme.addStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }
        else
        {
            applyTheme.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }
    }
}
