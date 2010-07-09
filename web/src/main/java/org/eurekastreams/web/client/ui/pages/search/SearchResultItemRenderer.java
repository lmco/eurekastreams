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
package org.eurekastreams.web.client.ui.pages.search;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.ui.common.pagedlist.GroupRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.ItemRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.OrganizationRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders an employee.
 */
public class SearchResultItemRenderer implements ItemRenderer<ModelView>
{
    /**
     * Renders a person.
     */
    private ItemRenderer<PersonModelView> personRenderer = new PersonRenderer(true);

    /**
     * Renders a group.
     */
    private ItemRenderer<DomainGroupModelView> groupRenderer = new GroupRenderer();

    /**
     * renders an org.
     */
    private ItemRenderer<OrganizationModelView> orgRenderer = new OrganizationRenderer();

    /**
     * Readable metadata keys.
     */
    private Map<String, String> humanReadableMetadataKeys = new HashMap<String, String>();


    /**
     * Constructor.
     *
     */
    public SearchResultItemRenderer()
    {
        humanReadableMetadataKeys.put("jobs", "Jobs");
        humanReadableMetadataKeys.put("background", "Skills and Specialties");
        humanReadableMetadataKeys.put("capabilities", "Capabilities");
        humanReadableMetadataKeys.put("education", "Education");
        humanReadableMetadataKeys.put("biography", "Biography");
        humanReadableMetadataKeys.put("overview", "Overview");
    }

    /**
     * @param result
     *            the result to render.
     *
     * @return the result as a widget.
     */
    public Panel render(final ModelView result)
    {
        Panel resultWidget;

        if (result instanceof PersonModelView)
        {
            resultWidget = personRenderer.render((PersonModelView) result);
        }
        else if (result instanceof DomainGroupModelView)
        {
            resultWidget = groupRenderer.render((DomainGroupModelView) result);
        }
        else if (result instanceof OrganizationModelView)
        {
            resultWidget = orgRenderer.render((OrganizationModelView) result);
        }
        else
        {
            resultWidget = new FlowPanel();
            resultWidget.add(new Label("Unknown Result Type"));
        }

        Object[] resultArr = result.getFieldMatch().getMatchedFieldKeys().toArray();

        FlowPanel matchedWidget = new FlowPanel();

        if (resultArr.length > 0)
        {
            Label resultsLbl =  new Label("Matches found in: ");
            matchedWidget.add(resultsLbl);
            matchedWidget.addStyleName("search-metadata");
            resultWidget.add(matchedWidget);
        }

        for (int i = 0; i < resultArr.length; i++)
        {
            Label keyLabel = new Label(humanReadableMetadataKeys.get(resultArr[i]));

            if (i + 1 < resultArr.length)
            {
                keyLabel.setText(keyLabel.getText() + ", ");
            }

            keyLabel.addStyleName("search-metadata-key");

            matchedWidget.add(keyLabel);
        }

        return resultWidget;
    }
}
