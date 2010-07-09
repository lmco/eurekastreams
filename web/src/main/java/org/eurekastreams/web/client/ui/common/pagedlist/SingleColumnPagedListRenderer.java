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
package org.eurekastreams.web.client.ui.common.pagedlist;

import java.io.Serializable;

import org.eurekastreams.server.domain.PagedSet;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders items into a single column.
 */
public class SingleColumnPagedListRenderer implements PagedListRenderer
{
    /**
     * {@inheritDoc}
     */
    public void render(final Panel renderContainer, final ItemRenderer itemRenderer,
            final PagedSet<? extends Serializable> items, final String noItemsMessage)
    {
        if (items.getTotal() == 0)
        {
            Label noItemsMessageLabel = new Label(noItemsMessage);
            noItemsMessageLabel.addStyleName("connection-item-empty");
            renderContainer.add(noItemsMessageLabel);
        }
        else
        {
            for (Serializable item : items.getPagedSet())
            {
                renderContainer.add(itemRenderer.render(item));
            }
        }
    }
}
