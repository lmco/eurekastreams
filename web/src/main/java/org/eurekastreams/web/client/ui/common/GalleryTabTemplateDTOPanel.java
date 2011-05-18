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
package org.eurekastreams.web.client.ui.common;

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.DeletedGalleryTabTemplateResponse;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GalleryTabTemplateModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Render panel for GalleryTabTemplateDTO.
 * 
 */
public class GalleryTabTemplateDTOPanel extends FlowPanel
{
    /**
     * The apply theme button.
     */
    private Label applyTab;

    /**
     * Constructor.
     * 
     * @param inItem
     *            GalleryTabTemplateDTO to display.
     */
    public GalleryTabTemplateDTOPanel(final GalleryTabTemplateDTO inItem)
    {
        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            EditPanel editControls = new EditPanel(this, Mode.EDIT_AND_DELETE);

            final HashMap<String, String> params = new HashMap<String, String>();
            params.put("action", "editTab");
            params.put("description", inItem.getDescription());
            params.put("category", inItem.getCategory().getName());
            params.put("id", String.valueOf(inItem.getId()));

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
                    if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete this tab? "))
                    {
                        GalleryTabTemplateModel.getInstance().delete(inItem.getId());

                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("The " + inItem.getTitle()
                                        + " tab has been deleted.")));
                    }
                }
            });
        }

        final GalleryTabTemplateDTOPanel thisBuffered = this;

        // listen for delete even and hide if appropriate.
        Session.getInstance().getEventBus().addObserver(DeletedGalleryTabTemplateResponse.class,
                new Observer<DeletedGalleryTabTemplateResponse>()
                {
                    public void update(final DeletedGalleryTabTemplateResponse arg1)
                    {
                        if (arg1.getResponse() == inItem.getId())
                        {
                            thisBuffered.setVisible(false);
                        }
                    }
                });

        Label title = new Label(inItem.getTitle());
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());

        applyTab = new Label("Apply");
        applyTab.addStyleName(StaticResourceBundle.INSTANCE.coreCss().applyTheme());
        applyTab.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                GalleryTabTemplateModel.getInstance().set(inItem.getId());
            }
        });

        add(new Label(inItem.getTitle()));
        add(new Label(inItem.getDescription()));
        add(new Label(inItem.getCategory().getName()));
        add(new Label(inItem.getCreated().toString()));
        add(applyTab);
    }
}
