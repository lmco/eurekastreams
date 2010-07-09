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
package org.eurekastreams.server.persistence;

import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Person;

/**
 * This class provides the mapper functionality for AppData entities.
 */
public class AppDataMapper extends DomainEntityMapper<AppData>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public AppDataMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Return the domain entity name for ORM to identify the table name.
     *
     * @return string of the name of the entity for table queries.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "AppData";
    }

    /**
     * {@inheritDoc}
     */
    public AppData findOrCreateByPersonAndGadgetDefinitionIds(final long gadgetDefinitionId, final String personId)
    {
        AppData outputAppData = null;
        Query query = getEntityManager().createQuery(
                "from AppData a where a.person.openSocialId = "
                        + ":openSocialId and a.gadgetDefinition.id = :gadgetDefinitionId").setParameter("openSocialId",
                personId).setParameter("gadgetDefinitionId", gadgetDefinitionId);

        if (query.getResultList().size() > 0)
        {
            outputAppData = (AppData) query.getSingleResult();
        }
        else
        {
            Query getPersonQuery = getEntityManager().createQuery("from Person p where p.openSocialId = :openSocialId")
                    .setParameter("openSocialId", personId);
            Query getGadgetDefQuery = getEntityManager().createQuery(
                    "from GadgetDefinition gd where gd.id = :gadgetDefinitionId").setParameter("gadgetDefinitionId",
                    gadgetDefinitionId);
            Person currentPerson = null;
            GadgetDefinition currentGadgetDefinition = null;

            if (getPersonQuery.getResultList().size() > 0 && getGadgetDefQuery.getResultList().size() > 0)
            {
                currentPerson = (Person) getPersonQuery.getSingleResult();
                currentGadgetDefinition = (GadgetDefinition) getGadgetDefQuery.getSingleResult();
                outputAppData = new AppData(currentPerson, currentGadgetDefinition);
                getEntityManager().persist(outputAppData);
            }

        }
        String openSocialId = outputAppData.getPerson().getOpenSocialId();
        return outputAppData;
    }

    /**
     * Delete an AppData value based on supplied key and appdata id.
     *
     * @param appDataId
     *            - long id of the owning application to the data to be removed.
     * @param appDataValueKey
     *            - string key of the app data value to delete.
     * @throws Exception
     *             if more than one item is deleted with the query.
     */
    public void deleteAppDataValueByKey(final long appDataId, final String appDataValueKey) throws Exception
    {
        int numResults = getEntityManager().createQuery(
                "delete from AppDataValue where name = :appDataValueKey" + " and appData.id = :appDataId")
                .setParameter("appDataValueKey", appDataValueKey).setParameter("appDataId", appDataId).executeUpdate();
        if (numResults > 1)
        {
            throw new Exception("More than one item was deleted with this request and only one was expected.");
        }
    }
}
