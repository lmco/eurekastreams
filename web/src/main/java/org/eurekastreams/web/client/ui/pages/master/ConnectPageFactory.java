/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.master;

import java.util.List;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.ui.pages.widget.CommentWidget;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates a page given a page and view.
 *
 */
public class ConnectPageFactory
{
    /**
     * Creates a page given a page and view.
     *
     * @param page
     *            the page.
     * @param views
     *            the views.
     * @return the page widget.
     */
    public Widget createPage(final Page page, final List<String> views)
    {
        RootPanel.get().setStyleName("");

        String view = "";
        if (!views.isEmpty())
        {
            view = views.get(0);
        }

        switch (page)
        {
        case WIDGET_COMMENT:
            return new CommentWidget(view);
        case WIDGET_LIKE_SHARE:
        case WIDGET_PROFILE_BADGE:
        case WIDGET_STREAM:
        default:
            return null;
        }
    }
}
