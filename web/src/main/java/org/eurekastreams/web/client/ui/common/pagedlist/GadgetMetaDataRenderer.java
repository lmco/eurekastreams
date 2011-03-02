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
package org.eurekastreams.web.client.ui.common.pagedlist;

import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.web.client.model.GadgetDefinitionModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.GadgetMetaDataPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Panel;

/**
 * Renderer for gadgets.
 *
 */
public class GadgetMetaDataRenderer implements ItemRenderer<GadgetMetaDataDTO>
{

    /**
     * Render the panel.
     *
     * @param item
     *            the gadget meta data.
     * @return the panel.
     */
    public Panel render(final GadgetMetaDataDTO item)
    {
        String tabParam = Session.getInstance().getParameterValue(StaticResourceBundle.INSTANCE.coreCss().tab());
        return new GadgetMetaDataPanel(item, tabParam != null ? Long.valueOf(tabParam) : null,
                GadgetDefinitionModel.getInstance(), "Are you sure you want to delete this app? "
                        + "Deleting this app will remove it from the start page of every user that has "
                        + "added it to a tab.");
    }

}
