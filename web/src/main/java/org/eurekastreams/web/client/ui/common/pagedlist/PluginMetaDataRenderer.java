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

import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.web.client.model.PluginDefinitionModel;
import org.eurekastreams.web.client.ui.common.GadgetMetaDataPanel;

import com.google.gwt.user.client.ui.Panel;

/**
 * Plugin meta data renderer.
 *
 */
public class PluginMetaDataRenderer implements ItemRenderer<GadgetMetaDataDTO>
{
    /**
     * Render the panel.
     * @param item the gadget meta data.
     * @return the panel.
     */
    public Panel render(final GadgetMetaDataDTO item)
    {
        return new GadgetMetaDataPanel(item, null,
                PluginDefinitionModel.getInstance(), "Are you sure you want to delete this plugin?");
    }

}
