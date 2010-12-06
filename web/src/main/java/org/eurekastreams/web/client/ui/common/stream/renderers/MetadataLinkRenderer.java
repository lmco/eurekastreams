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
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders a metadata link like By Joe Smith.
 *
 */
public class MetadataLinkRenderer
{
    /**
     * JSNI Facade.
     */
    private WidgetJSNIFacadeImpl jSNIFacade = new WidgetJSNIFacadeImpl();
    /**
     * Whether or not to display the link.
     */
    private boolean display = true;
    /**
     * The label.
     */
    private String label;
    /**
     * The type the entity is.
     */
    private EntityType type;
    /**
     * The id of the entity.
     */
    private String id;
    /**
     * the name of the entity.
     */
    private String name;

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
     * @param inDisplay
     *            whether to display it.
     */
    public MetadataLinkRenderer(final String inLabel, final EntityType inType, final String inId, final String inName,
            final boolean inDisplay)
    {
        label = inLabel;
        type = inType;
        id = inId;
        name = inName;
        display = inDisplay;
    }

    /**
     * Constructor to use when display is always true.
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
        this(inLabel, inType, inId, inName, true);
    }

    /**
     * Constructor to use when display is always true and type is always person.
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
        this(inLabel, EntityType.PERSON, inId, inName, true);
    }

    /**
     * Returns a link with a label.
     *
     * @return the link.
     */
    public Widget render()
    {
        if (id != null && display)
        {
            if (type.equals(EntityType.PLUGIN) || type.equals(EntityType.APPLICATION))
            {
                return new Hyperlink(name, id);
            }
            else
            {
                Page page = type.equals(EntityType.GROUP) ? Page.GROUPS : Page.PEOPLE;
                String url = Session.getInstance().generateUrl(new CreateUrlRequest(page, id));
                return new Hyperlink(jSNIFacade.escapeHtml(name), url);
            }
        }
        else
        {
            return null;
        }
    }
}
