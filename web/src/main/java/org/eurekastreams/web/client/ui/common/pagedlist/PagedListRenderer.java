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

import com.google.gwt.user.client.ui.Panel;

/**
 * Lays out items for a paged list.
 *
 */
public interface PagedListRenderer
{
    /**
     * Renders the items per the renderer's layout style.
     *
     * @param renderContainer
     *            Container to render into.
     * @param itemRenderer
     *            Renderer for the items.
     * @param items
     *            The items.
     * @param noItemsMessage
     *            Message when there are no items.
     */
    void render(Panel renderContainer, ItemRenderer itemRenderer, final PagedSet<? extends Serializable> items,
            final String noItemsMessage);
}
