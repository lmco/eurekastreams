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
package org.eurekastreams.web.client.ui.common.pagedlist;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders a person or a group.
 */
public class ModelViewRenderer implements ItemRenderer<ModelView>
{
    /**
     * Person renderer.
     */
    private PersonRenderer personRenderer = new PersonRenderer(false);

    /**
     * Group renderer.
     */
    private GroupRenderer groupRenderer = new GroupRenderer();

    /**
     * Render the item.
     * 
     * @param item
     *            the item.
     * @return the rendered item.
     */
    public Panel render(final ModelView item)
    {
        if (item instanceof PersonModelView)
        {
            return personRenderer.render((PersonModelView) item);
        }

        if (item instanceof DomainGroupModelView)
        {
            return groupRenderer.render((DomainGroupModelView) item);
        }

        FlowPanel unhandled = new FlowPanel();
        unhandled.add(new Label("Unhandled Type."));
        return unhandled;
    }

}
