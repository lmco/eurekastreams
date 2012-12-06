/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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

import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

import com.google.gwt.user.client.ui.Widget;

/**
 * Common code for activity rendering.
 */
public final class RenderUtilities
{
    /** Forbid instantiation. */
    private RenderUtilities()
    {
    }

    /**
     * Supplies renderers for the activity actor name.
     *
     * @param renderers
     *            List to add renderers to.
     * @param activity
     *            Activity to render.
     */
    public static void addActorNameRenderers(final List<StatefulRenderer> renderers, final ActivityDTO activity)
    {
        addEntityNameRenderers(renderers, activity.getActor(), null);
    }

    /**
     * Supplies renderers for a stream name.
     *
     * @param renderers
     *            List to add renderers to.
     * @param entity
     *            Stream entity whose name to render.
     * @param label
     *            Label to place before the name.
     */
    public static void addEntityNameRenderers(final List<StatefulRenderer> renderers, final StreamEntityDTO entity,
            final String label)
    {
        renderers.add(getEntityNameRenderer(entity, label));
    }

    /**
     * Gets the renderer for a stream name.
     *
     * @param entity
     *            Stream entity whose name to render.
     * @param label
     *            Label to place before the name.
     * @return Renderer for stream name.
     */
    public static StatefulRenderer getEntityNameRenderer(final StreamEntityDTO entity, final String label)
    {
        String name = entity.getDisplayName();
        if (entity.isActive())
        {
            return new MetadataLinkRenderer(label, entity.getType(), entity.getUniqueIdentifier(), name);
        }
        else
        {
            return new SimpleTextRenderer(label == null || label.isEmpty() ? name : label + " " + name);
        }
    }

    /**
     * Gets the renderer for a stream name.
     * 
     * @param entity
     *            Stream entity whose name to render.
     * @param label
     *            Label to place before the name.
     * @return Rendered widget.
     */
    public static Widget renderEntityName(final StreamEntityDTO entity, final String label)
    {
        return getEntityNameRenderer(entity, label).render();
    }
}
