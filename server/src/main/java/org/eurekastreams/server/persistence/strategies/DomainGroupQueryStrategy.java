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
package org.eurekastreams.server.persistence.strategies;

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.search.factories.DomainGroupModelViewFactory;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Strategy for building up a DomainGroupModelView from the DomainGroup entity.
 */
public class DomainGroupQueryStrategy
{
    /**
     * Build the base Criteria object.
     * 
     * @param hibernateSession
     *            the hibernate session used to create the criteria.
     * @return the base criteria object.
     */
    public Criteria getCriteria(final Session hibernateSession)
    {
        Criteria criteria = hibernateSession.createCriteria(DomainGroup.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(getColumn("description"));
        fields.add(getColumn("name"));
        fields.add(getColumn("shortName"));
        fields.add(Projections.property("publicGroup").as("isPublic"));
        fields.add(getColumn("updatesCount"));
        fields.add(getColumn("followersCount"));
        fields.add(getColumn("dateAdded"));
        fields.add(getColumn("avatarId"));
        fields.add(getColumn("avatarCropSize"));
        fields.add(getColumn("avatarCropX"));
        fields.add(getColumn("avatarCropY"));
        fields.add(getColumn("bannerId"));
        fields.add(getColumn("url"));
        fields.add(getColumn("overview"));
        fields.add(getColumn("commentable"));
        fields.add(getColumn("streamPostable"));
        fields.add(getColumn("suppressPostNotifToMember"));
        fields.add(getColumn("suppressPostNotifToCoordinator"));
        fields.add(Projections.property("cb.accountId").as("personCreatedByAccountId"));
        fields.add(Projections.property("cb.displayName").as("personCreatedByDisplayName"));
        fields.add(Projections.property("stream.id").as("streamId"));
        criteria.setProjection(fields);
        criteria.createAlias("createdBy", "cb");
        criteria.createAlias("streamScope", "stream");

        // We don't currently cache pending groups
        // TODO: this needs to change - we should figure out how to remove this rule
        criteria.add(Restrictions.eq("isPending", false));

        ModelViewResultTransformer<DomainGroupModelView> resultTransformer = //
        new ModelViewResultTransformer<DomainGroupModelView>(new DomainGroupModelViewFactory());
        criteria.setResultTransformer(resultTransformer);

        return criteria;
    }

    /**
     * Build the PropertyProjection with alias.
     * 
     * @param propertyName
     *            the property name
     * @return the PropertyProjection with alias
     */
    private Projection getColumn(final String propertyName)
    {
        return Projections.property(propertyName).as(propertyName);
    }
}
