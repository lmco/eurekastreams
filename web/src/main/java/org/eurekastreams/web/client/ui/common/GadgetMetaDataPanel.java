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

import org.eurekastreams.server.action.request.start.ReorderGadgetRequest;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.GadgetAddedToStartPageEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.SwitchToFilterOnPagedFilterPanelEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.model.requests.AddGadgetToStartPageRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Displays gadget metadata for the gallery. (and anything else I guess).
 * 
 */
public class GadgetMetaDataPanel extends FlowPanel
{
    /**
     * The tab id.
     */
    private Long tabId;

    /**
     * Apply Gadget link.
     */
    private Hyperlink applyGadget;

    /**
     * Drop zone id.
     */
    private Integer dropZoneId = null;

    /**
     * Default constructor.
     * 
     * @param metaData
     *            the gadget meta data.
     * @param inTabId
     *            the tab id.
     * @param model
     *            the model to delete from.
     * @param deleteMessage
     *            the delete message.
     */
    public GadgetMetaDataPanel(final GadgetMetaDataDTO metaData, final Long inTabId, final Deletable<Long> model,
            final String deleteMessage)
    {
        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (event.getParameters().get("dropzone") != null)
                        {
                            dropZoneId = Integer.valueOf(event.getParameters().get("dropzone"));
                        }
                        else
                        {
                            dropZoneId = null;
                        }
                    }
                }, true);

        tabId = inTabId;
        final FlowPanel thisBuffered = this;

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            EditPanel editControls = new EditPanel(this, Mode.EDIT_AND_DELETE);

            if (tabId != null)
            {
                final HashMap<String, String> params = new HashMap<String, String>();
                params.put("action", "editApp");
                params.put("url", metaData.getGadgetDefinition().getUrl());
                params.put("category", metaData.getGadgetDefinition().getCategory().toString());
                params.put("id", String.valueOf(metaData.getGadgetDefinition().getId()));
                params.put("tab", Session.getInstance().getParameterValue("tab"));
                final WidgetJSNIFacadeImpl jsni = new WidgetJSNIFacadeImpl();

                editControls.addEditClickHandler(new ClickHandler()
                {
                    public void onClick(final ClickEvent event)
                    {
                        jsni.setHistoryToken(Session.getInstance().generateUrl(
                                new CreateUrlRequest(Page.GALLERY, params)), true);
                    }
                });
            }

            this.add(editControls);

            editControls.addDeleteClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (new WidgetJSNIFacadeImpl().confirm(deleteMessage))
                    {
                        model.delete(metaData.getGadgetDefinition().getId());

                        Session.getInstance().getEventBus()
                                .notifyObservers(
                                        new ShowNotificationEvent(new Notification(metaData.getTitle()
                                                + " has been deleted.")));
                        thisBuffered.setVisible(false);
                    }
                }
            });

        }

        FlowPanel imageContainer = new FlowPanel();
        imageContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().imageContainer());

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().gadgetMetaData());

        // Im a gadget.
        if (tabId != null)
        {
            if (metaData.getThumbnail() != null && !metaData.getThumbnail().equals(""))
            {
                imageContainer.add(new Image(metaData.getThumbnail()));
            }
            else
            {
                imageContainer.add(new Image("/style/images/gadget-gallery-default.png"));
            }

            applyGadget = new Hyperlink("Apply App", History.getToken());
            applyGadget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().applyGadget());
            applyGadget.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    GadgetModel.getInstance()
                            .insert(
                                    new AddGadgetToStartPageRequest("{" + metaData.getGadgetDefinition().getUUID()
                                            + "}", tabId));
                }
            });

            Session.getInstance().getEventBus().addObserver(GadgetAddedToStartPageEvent.class,
                    new Observer<GadgetAddedToStartPageEvent>()
                    {

                        public void update(final GadgetAddedToStartPageEvent arg1)
                        {
                            Session.getInstance().getEventBus().notifyObservers(
                                    new ShowNotificationEvent(new Notification("App has been added")));

                            if (arg1.getGadget().getGadgetDefinition().getId() == metaData.getGadgetDefinition()
                                    .getId())
                            {
                                setActive(true);

                                if (dropZoneId != null)
                                {
                                    GadgetModel.getInstance().reorder(
                                            new ReorderGadgetRequest(tabId, new Long(arg1.getGadget().getId()),
                                                    dropZoneId, 0));
                                }
                            }
                        }
                    });

            imageContainer.add(applyGadget);
        }
        // Im a plugin
        else
        {
            if (metaData.getScreenshot() != null && !metaData.getScreenshot().equals(""))
            {
                FlowPanel screenShot = new FlowPanel();
                screenShot.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamPluginsScreenshot());
                imageContainer.add(screenShot);
                screenShot.add(new Image(metaData.getScreenshot()));
            }
        }

        FlowPanel dataPanel = new FlowPanel();
        dataPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().gadgetData());

        Label title = new Label(metaData.getTitle());
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        dataPanel.add(title);
        dataPanel.add(new HTML(metaData.getDescription()));
        Anchor titleUrl = new Anchor(metaData.getTitleUrl(), metaData.getTitleUrl(), "_blank");
        titleUrl.addStyleName(StaticResourceBundle.INSTANCE.coreCss().gadgetTitleUrl());

        dataPanel.add(titleUrl);
        FlowPanel gadgetExtInfo = new FlowPanel();
        gadgetExtInfo.addStyleName(StaticResourceBundle.INSTANCE.coreCss().gadgetExtInfo());
        gadgetExtInfo.add(new HTML("Category: "));
        Anchor category = new Anchor();
        category.setText(metaData.getGadgetDefinition().getCategory().getName());
        category.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent arg0)
            {
                EventBus.getInstance().notifyObservers(
                        new SwitchToFilterOnPagedFilterPanelEvent("gadgets", metaData.getGadgetDefinition()
                                .getCategory().getName(), "Recent"));
            }
        });

        gadgetExtInfo.add(category);
        insertActionSeparator(gadgetExtInfo);
        gadgetExtInfo.add(new HTML(" Users: <span class='light'>" + metaData.getGadgetDefinition().getNumberOfUsers()
                + "</span>"));
        insertActionSeparator(gadgetExtInfo);
        gadgetExtInfo.add(new HTML(" Author: <a href='mailto:" + metaData.getAuthorEmail() + "'>"
                + metaData.getAuthor() + "</a>"));
        insertActionSeparator(gadgetExtInfo);
        gadgetExtInfo.add(new HTML(" Publish date: <span class='light'>"
                + DateTimeFormat.getLongDateFormat().format(metaData.getGadgetDefinition().getCreated()) + "</span>"));
        dataPanel.add(gadgetExtInfo);

        this.add(imageContainer);
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
     * Sets the theme as active or not.
     * 
     * @param active
     *            value.
     */
    public void setActive(final Boolean active)
    {
        if (active)
        {
            applyGadget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }
        else
        {
            applyGadget.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }
    }
}
