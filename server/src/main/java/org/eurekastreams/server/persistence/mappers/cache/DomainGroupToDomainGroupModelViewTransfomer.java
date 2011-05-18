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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Transform DomainGroup to DomainGroupModelView. NOTE: This is a minimal transformation, expand as needed.
 * 
 */
public class DomainGroupToDomainGroupModelViewTransfomer implements Transformer<DomainGroup, DomainGroupModelView>
{

    /**
     * Transform DomainGroup to DomainGroupModelView. NOTE: This is a minimal transformation, expand as needed.
     * 
     * @param inTransformType
     *            DomainGroup to transform.
     * @return DomainGroupModelView created from param DomainGroup.
     */
    @Override
    public DomainGroupModelView transform(final DomainGroup inTransformType)
    {
        DomainGroupModelView result = new DomainGroupModelView();

        result.setShortName(inTransformType.getShortName());
        result.setPending(inTransformType.isPending());

        return result;
    }

}
