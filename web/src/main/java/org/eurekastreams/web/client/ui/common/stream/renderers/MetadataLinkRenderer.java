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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a metadata link like By Joe Smith.
 *
 */
public class MetadataLinkRenderer
{
    /**
     * The label.
     */
    private final String label;
    /**
     * The type the entity is.
     */
    private final EntityType type;
    /**
     * The id of the entity.
     */
    private final String id;
    /**
     * the name of the entity.
     */
    private final String name;

    /**
     * Default constructor.
     *
     * @param inLabel
     *            the label.
     * @param inType
     *            the type.
     * @param inId
     *            the id.
     * @param inName
     *            the name.
     */
    public MetadataLinkRenderer(final String inLabel, final EntityType inType, final String inId, final String inName)
    {
        label = inLabel;
        type = inType;
        id = inId;
        name = inName;
    }

    /**
     * Constructor to for people.
     * 
     * @param inLabel
     *            the label.
     * @param inId
     *            the id.
     * @param inName
     *            the name.
     */
    public MetadataLinkRenderer(final String inLabel, final String inId, final String inName)
    {
        this(inLabel, EntityType.PERSON, inId, inName);
    }

    /**
     * Returns a link with a label.
     *
     * @return the link.
     */
    public Widget render()
    {
        if (id != null)
        {
            String url;

            if (type.equals(EntityType.PLUGIN) || type.equals(EntityType.APPLICATION))
            {
                // TODO: Is this correct for the new URL scheme?
                url = id;
            }
            else
            {
                Page page = type.equals(EntityType.GROUP) ? Page.GROUPS : Page.PEOPLE;
                url = Session.getInstance().generateUrl(new CreateUrlRequest(page, id));
            }

            if (label != null && !label.isEmpty())
            {
                Panel main = new FlowPanel();
                main.addStyleName("inline-panel");
                main.add(new InlineLabel(label + " "));
                main.add(new InlineHyperlink(name, url));
                return main;
            }
            else
            {
                return new InlineHyperlink(name, url);
            }
        }
        else
        {
            return null;
        }
    }
}
