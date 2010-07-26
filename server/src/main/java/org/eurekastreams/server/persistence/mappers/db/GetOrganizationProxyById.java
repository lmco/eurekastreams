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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to return hibernate proxy representing an organization. This is done for performance to prevent hits to db.
 * 
 */
public class GetOrganizationProxyById extends BaseArgDomainMapper<Long, Organization>
{

    /**
     * Return Hibernate proxy representing an organization.
     * 
     * @param inRequest
     *            the org id.
     * @return hibernate proxy object representing the organization.
     */
    @Override
    public Organization execute(final Long inRequest)
    {
        return (Organization) getHibernateSession().load(Organization.class, inRequest);
    }

}
