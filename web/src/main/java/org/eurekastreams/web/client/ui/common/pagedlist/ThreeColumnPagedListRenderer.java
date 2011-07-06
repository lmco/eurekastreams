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

import java.io.Serializable;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders items into three columns.
 */
public class ThreeColumnPagedListRenderer implements PagedListRenderer
{
    /**
     * {@inheritDoc}
     */
    public void render(final Panel renderContainer, final ItemRenderer itemRenderer,
            final PagedSet< ? extends Serializable> items, final String noItemsMessage)
    {
        Panel left = new FlowPanel();
        left.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionColLeft());

        Panel middle = new FlowPanel();
        middle.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionColMiddle());

        Panel right = new FlowPanel();
        right.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionColRight());

        if (items.getTotal() == 0)
        {
            Label noItemsMessageLabel = new Label(noItemsMessage);
            noItemsMessageLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemEmpty());
            renderContainer.add(noItemsMessageLabel);
        }
        else
        {
            renderContainer.add(left);
            renderContainer.add(middle);
            renderContainer.add(right);
        }
        double thirdCol = items.getPagedSet().size() * 2.0 / 3.0;
        double secondCol = items.getPagedSet().size() / 3.0;

        int count = 0;
        for (Serializable item : items.getPagedSet())
        {
            if (count >= thirdCol)
            {
                right.add(itemRenderer.render(item));
            }
            else if (count >= secondCol)
            {
                middle.add(itemRenderer.render(item));
            }
            else
            {
                left.add(itemRenderer.render(item));
            }

            count++;
        }
    }
}