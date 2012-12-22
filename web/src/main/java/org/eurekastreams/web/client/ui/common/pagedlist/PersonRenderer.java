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

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.ui.common.PersonPanel;

import com.google.gwt.user.client.ui.Panel;
/**
 * Renders people.
 *
 */
public class PersonRenderer implements ItemRenderer<PersonModelView>
{
    /** If the description line should be shown. */
    private boolean showDescription;

    /**
     * Constructor.
     * 
     * @param inShowDescription
     *            If the description line should be shown.
     */
    public PersonRenderer(final boolean inShowDescription)
    {
        showDescription = inShowDescription;
    }

    /**
     * {@inheritDoc}
     */
    public Panel render(final PersonModelView item)
    {
        return new PersonPanel(item, false, showDescription, false);
    }

}
