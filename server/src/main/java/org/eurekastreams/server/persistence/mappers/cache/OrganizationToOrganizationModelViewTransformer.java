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

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Transform Organization to OrganizationModelView. NOTE: This is a minimal transformation, expand as needed.
 * 
 */
public class OrganizationToOrganizationModelViewTransformer implements Transformer<Organization, OrganizationModelView>
{

    /**
     * Transform Organization to OrganizationModelView. NOTE: This is a minimal transformation, expand as needed.
     * 
     * @param inTransformType
     *            Organization to transform.
     * @return OrganizationModelView created from param Organization.
     */
    @Override
    public OrganizationModelView transform(final Organization inTransformType)
    {
        OrganizationModelView result = new OrganizationModelView();

        result.setShortName(inTransformType.getShortName());
        result.setName(inTransformType.getName());
        result.setEntityId(inTransformType.getId());

        return result;
    }

}
