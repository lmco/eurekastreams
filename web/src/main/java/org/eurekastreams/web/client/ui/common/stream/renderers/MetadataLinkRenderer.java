/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a metadata link like By Joe Smith.
 */
public class MetadataLinkRenderer implements StatefulRenderer
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

            switch (type)
            {
            case PERSON:
                url = Session.getInstance().generateUrl(new CreateUrlRequest(Page.PEOPLE, id));
                break;
            case GROUP:
                url = Session.getInstance().generateUrl(new CreateUrlRequest(Page.GROUPS, id));
                break;
            case PLUGIN:
            case APPLICATION:
                // TODO: Is this correct for the new URL scheme?
                url = id;
                break;
            case RESOURCE:
            case NOTSET:
                url = id;
                break;
            default:
                url = null;
            }

            if (url == null || url.isEmpty())
            {
                return new InlineLabel(label == null || label.isEmpty() ? name : label + " " + name);
            }
            else if (label == null || label.isEmpty())
            {
                return new InlineHyperlink(name, url);
            }
            else
            {
                Panel main = new FlowPanel();
                main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().inlinePanel());
                main.add(new InlineLabel(label + " "));
                main.add(new InlineHyperlink(name, url));
                return main;
            }
        }
        else
        {
            return null;
        }
    }
}
