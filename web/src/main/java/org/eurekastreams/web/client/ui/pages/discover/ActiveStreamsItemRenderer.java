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
package org.eurekastreams.web.client.ui.pages.discover;

import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.web.client.ui.common.pagedlist.ItemRenderer;

import com.google.gwt.user.client.ui.Widget;

/**
 * Renderer for StreamDTOs as Most Active Streams Items..
 */
public class ActiveStreamsItemRenderer implements ItemRenderer<StreamDTO>
{
    /**
     * Render an ActiveStreamsItemPanel for the input StreamDTO.
     *
     * @param inStreamDTO
     *            StreamDTO to render
     * @return the rendered panel
     */
    public Widget render(final StreamDTO inStreamDTO)
    {
        return new ActiveStreamItemPanel(inStreamDTO);
    }
}
