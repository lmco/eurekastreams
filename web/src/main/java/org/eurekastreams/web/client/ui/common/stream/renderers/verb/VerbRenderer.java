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
package org.eurekastreams.web.client.ui.common.stream.renderers.verb;

import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.web.client.ui.common.stream.renderers.MetadataLinkRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.object.ObjectRenderer;

import com.google.gwt.user.client.ui.Widget;

/**
 * Verb renderer.
 *
 */
public interface VerbRenderer
{
    /**
     * Should the verb allow commenting.
     *
     * @return the value.
     */
    boolean getAllowComment();

    /**
     * Should the verb allow starring.
     *
     * @return the value.
     */
    boolean getAllowStar();
    
    /**
     * Should the ver allow liking.
     * 
     * @return the value.
     */
    boolean getAllowLike();
    
    /**
     * Should the verb allow sharing.
     *
     * @return the value.
     */
    boolean getAllowShare();

    /**
     * Get the avatar.
     *
     * @return the avatar.
     */
    Widget getAvatar();

    /**
     * Get the content.
     *
     * @return the content.
     */
    Widget getContent();

    /**
     * Retern a list of appropriate metadata link renderers.
     *
     * @return the list.
     */
    List<MetadataLinkRenderer> getMetaDataItemRenderers();

    /**
     * Retern a list of appropriate metadata link renderers for the source (by X to Y).
     *
     * @return the list.
     */
    List<MetadataLinkRenderer> getSourceMetaDataItemRenderers();

    /**
     * Setup.
     *
     * @param objectRendererDictionary
     *            object dictionary.
     * @param activity
     *            the activity.
     * @param state
     *            the state of the activity.
     * @param showRecipient
     *            the recipient.
     */
    void setup(
            final HashMap<BaseObjectType, ObjectRenderer> objectRendererDictionary,
            final ActivityDTO activity,
            final StreamMessageItemRenderer.State state,
            final boolean showRecipient);
}
