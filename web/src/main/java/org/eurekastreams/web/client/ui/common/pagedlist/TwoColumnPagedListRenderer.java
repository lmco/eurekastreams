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
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders items into two columns.
 */
public class TwoColumnPagedListRenderer implements PagedListRenderer
{
    /**
     * {@inheritDoc}
     */
    public void render(final Panel renderContainer, final ItemRenderer itemRenderer,
            final PagedSet<? extends Serializable> items, final String noItemsMessage)
    {
        Panel left = new FlowPanel();
        left.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionColLeft());
        Panel right = new FlowPanel();
        right.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionColRight());

        int count = 0;

        if (items.getTotal() == 0)
        {
            Label noItemsMessageLabel = new Label(noItemsMessage);
            noItemsMessageLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemEmpty());
            renderContainer.add(noItemsMessageLabel);
        }
        else
        {
            renderContainer.add(left);
            renderContainer.add(right);
        }
        double halfwayPoint = items.getPagedSet().size() / 2.0;

        for (Serializable item : items.getPagedSet())
        {
            if (count >= halfwayPoint)
            {
                right.add(itemRenderer.render(item));
            }
            else
            {
                left.add(itemRenderer.render(item));
            }

            count++;
        }
    }
}
