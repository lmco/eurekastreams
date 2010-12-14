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

import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Deletes a gadget definition (and all the gadgets using it).
 */
public class DeleteGadgetDefinition extends BaseArgDomainMapper<Long, Void>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Void execute(final Long inRequest)
    {
        // The relationship defined on GadgetDefinition to Gadget includes only the "undeleted" gadgets (ones whose
        // "deleted" flag is false), so deleting the GadgetDefinition will only cascade the delete to that subset of
        // Gadgets. So if there are any Gadgets where deleted is true, they are not deleted, and the foreign key
        // constraint prevents the GadgetDefinition from being deleted.

        // The "if deleted = false" may be convenient for queries - you can retrieve a gadget definition and don't have
        // to think about filtering out deleted gadgets, but it results in truly messed up table relationships. The
        // proper way to resolve this is to remove that clause, but since I don't want to track down and resolve all the
        // places that currently assume the gadgets are pre-filtered at this point, we just manually delete the Gadgets
        // (children) before deleting the GadgetDefinition (parent).
        // TODO: Resolve this properly as described above
        getEntityManager().createQuery("DELETE Gadget WHERE gadgetDefinition.id=:id").setParameter("id", inRequest)
                .executeUpdate();

        getEntityManager().remove(getHibernateSession().load(GadgetDefinition.class, inRequest));
        return null;
    }
}
