/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.factories.PersonModelViewFactory;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

/**
 * Strategy for building up a PersonModelView from the Person entity.
 */
public class PersonQueryStrategy
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
        Criteria criteria = hibernateSession.createCriteria(Person.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(getColumn("dateAdded"));
        fields.add(getColumn("accountId"));
        fields.add(getColumn("openSocialId"));
        fields.add(getColumn("avatarId"));
        fields.add(getColumn("avatarCropX"));
        fields.add(getColumn("avatarCropY"));
        fields.add(getColumn("avatarCropSize"));
        fields.add(getColumn("lastName"));
        fields.add(getColumn("displayNameSuffix"));
        fields.add(getColumn("displayName"));
        fields.add(getColumn("preferredName"));
        fields.add(getColumn("jobDescription"));
        fields.add(getColumn("title"));
        fields.add(getColumn("overview"));
        fields.add(getColumn("followersCount"));
        fields.add(getColumn("followingCount"));
        fields.add(getColumn("groupsCount"));
        fields.add(getColumn("optOutVideoIds"));
        fields.add(getColumn("updatesCount"));
        fields.add(getColumn("email"));
        fields.add(getColumn("commentable"));
        fields.add(getColumn("companyName"));
        fields.add(getColumn("streamPostable"));
        fields.add(getColumn("additionalProperties"));
        fields.add(getColumn("groupStreamHiddenLineIndex"));
        fields.add(getColumn("lastAcceptedTermsOfService"));
        fields.add(getColumn("accountLocked"));
        fields.add(getColumn("accountDeactivated"));
        fields.add(getColumn("workPhone"));
        fields.add(getColumn("cellPhone"));
        fields.add(getColumn("fax"));
        fields.add(Projections.property("streamViewHiddenLineIndex").as("compositeStreamHiddenLineIndex"));
        fields.add(Projections.property("stream.id").as("streamId"));
        criteria.setProjection(fields);
        criteria.createAlias("streamScope", "stream");

        ModelViewResultTransformer<PersonModelView> resultTransformer = new ModelViewResultTransformer<PersonModelView>(
                new PersonModelViewFactory());
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
