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
package org.eurekastreams.server.service.actions.strategies.galleryitem;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.GalleryItem;

/**
 * Creates and returns a gallery item.
 *
 * @param <T>
 *            the type of gallery item.
 */
public interface GalleryItemProvider<T extends GalleryItem>
{
    /**
     * Creates and returns a gallery item.
     *
     * @param inPrincipalActionContext
     *            the parameters that were passed into the action
     * @param inParams
     *            the parameters passed from the client
     * @return the gallery item
     */
    T provide(final PrincipalActionContext inPrincipalActionContext, final Map<String, Serializable> inParams);
}
