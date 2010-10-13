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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.search.factories.OrganizationModelViewFactory;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Gets a list of organization objects for a given list of org ids.
 */
public class GetOrganizationsByIdsDbMapper extends BaseArgDomainMapper<List<Long>, List<OrganizationModelView>>
{
    /**
     * Looks in cache for the necessary DTOs and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     *
     * @param ids
     *            the list of ids that should be found.
     * @return list of DTO objects.
     */
    @SuppressWarnings("unchecked")
    public List<OrganizationModelView> execute(final List<Long> ids)
    {
        if (ids == null || ids.size() == 0)
        {
            return new ArrayList<OrganizationModelView>();
        }

        Criteria criteria = getHibernateSession().createCriteria(Organization.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(Projections.property("parentOrganization.id").as("parentOrganizationId"));
        fields.add(getColumn("description"));
        fields.add(getColumn("name"));
        fields.add(getColumn("shortName"));
        fields.add(getColumn("childOrganizationCount"));
        fields.add(getColumn("descendantGroupCount"));
        fields.add(getColumn("descendantEmployeeCount"));
        fields.add(Projections.property("employeeFollowerCount").as("followersCount"));
        fields.add(getColumn("updatesCount"));
        fields.add(getColumn("avatarId"));
        fields.add(getColumn("overview"));
        fields.add(Projections.property("stream.id").as("streamId"));
        fields.add(getColumn("bannerId"));
        criteria.setProjection(fields);

        criteria.createAlias("streamScope", "stream");

        // Creates the necessary "OR" clauses to get all uncached items
        Criterion restriction = null;
        for (Long orgId : ids)
        {
            if (restriction == null)
            {
                restriction = Restrictions.eq("this.id", orgId);
            }
            else
            {
                restriction = Restrictions.or(Restrictions.eq("this.id", orgId), restriction);
            }
        }
        criteria.add(restriction);

        ModelViewResultTransformer<OrganizationModelView> resultTransformer = //
        new ModelViewResultTransformer<OrganizationModelView>(new OrganizationModelViewFactory());
        criteria.setResultTransformer(resultTransformer);

        return criteria.list();
    }
}
