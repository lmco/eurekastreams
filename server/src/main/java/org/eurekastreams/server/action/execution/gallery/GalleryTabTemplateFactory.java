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
package org.eurekastreams.server.action.execution.gallery;

import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Factory to create new GalleryTabTemplate, implements DomainMapper interface with same types as FindById mapper so it
 * can be easily interchanged for find or create functions.
 * 
 */
public class GalleryTabTemplateFactory implements DomainMapper<FindByIdRequest, GalleryTabTemplate>
{

    /**
     * Returns a new GalleryTabTemplate object.
     * 
     * @param inRequest
     *            ignored.
     * @return GalleryTabTemplate object.
     */
    @Override
    public GalleryTabTemplate execute(final FindByIdRequest inRequest)
    {
        return new GalleryTabTemplate();
    }

}
