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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.AddedFeaturedStreamResponseEvent;
import org.eurekastreams.web.client.model.FeaturedStreamModel;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextAreaFormElement;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content for creating or editing a stream view.
 */
public class FeatureDialogContent extends BaseDialogContent
{

    /**
     * Container flow panel.
     */
    private final FlowPanel container = new FlowPanel();

    /**
     * Main flow panel.
     */
    private final FlowPanel body = new FlowPanel();

    /**
     * Default constructor.
     * 
     * @param featuredStreamDTO
     *            the featured stream.
     */
    public FeatureDialogContent(final FeaturedStreamDTO featuredStreamDTO)
    {
        Label saveButton = new Label("");
        saveButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().saveChangesButton());

        body.add(new Label("Stream Name: " + featuredStreamDTO.getDisplayName()));

        final BasicTextAreaFormElement textArea = new BasicTextAreaFormElement(Person.MAX_JOB_DESCRIPTION_LENGTH,
                "Description", "description", featuredStreamDTO.getDescription(), "", true);

        body.add(textArea);
        body.add(saveButton);

        saveButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                featuredStreamDTO.setDescription(textArea.getValue());
                FeaturedStreamModel.getInstance().insert(featuredStreamDTO);
            }
        });

        EventBus.getInstance().addObserver(AddedFeaturedStreamResponseEvent.class,
                new Observer<AddedFeaturedStreamResponseEvent>()
                {
                    public void update(final AddedFeaturedStreamResponseEvent event)
                    {
                        close();
                    }
                });

        container.add(body);

    }

    /**
     * Gets the body panel.
     * 
     * @return the body.
     */
    public Widget getBody()
    {
        return container;
    }

    /**
     * Gets the CSS name.
     * 
     * @return the class.
     */
    @Override
    public String getCssName()
    {
        return StaticResourceBundle.INSTANCE.coreCss().featureDialog();
    }

    /**
     * Gets the title.
     * 
     * @return the title.
     */
    public String getTitle()
    {
        return "Feature Stream";
    }
}
