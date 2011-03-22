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
package org.eurekastreams.server.action.principal;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Create principal from datastore (cache, db, etc.).
 * 
 */
public class DataStorePrincipalPopulator implements PrincipalPopulator
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Person mapper.
     */
    private DomainMapper<String, PersonModelView> getPersonMVByAccountId;

    /**
     * {@link ActionController}.
     */
    private ActionController serviceActionController;

    /**
     * Action to create user from LDAP.
     */
    private TaskHandlerServiceAction createUserfromLdapAction;

    /**
     * Constructor.
     * 
     * @param inGetPersonMVByAccountId
     *            Person mapper.
     * @param inServiceActionController
     *            {@link ActionController}.
     * @param inCreateUserfromLdapAction
     *            Action to create user from LDAP.
     */
    public DataStorePrincipalPopulator(final DomainMapper<String, PersonModelView> inGetPersonMVByAccountId,
            final ActionController inServiceActionController, final TaskHandlerServiceAction inCreateUserfromLdapAction)
    {
        getPersonMVByAccountId = inGetPersonMVByAccountId;
        serviceActionController = inServiceActionController;
        createUserfromLdapAction = inCreateUserfromLdapAction;
    }

    /**
     * Retrieve the {@link Principal} object for accountid found in data store.
     * 
     * {@inheritDoc}.
     */
    @Override
    public Principal getPrincipal(final String inAccountId, final String inSessionId)
    {
        DefaultPrincipal result = null;
        try
        {
            // grab info from cache/db.
            PersonModelView pmv = getPersonMVByAccountId.execute(inAccountId);

            // if found, create principal, else attempt to create from LDAP (creates locked user in DB).
            if (pmv != null)
            {
                result = new DefaultPrincipal(pmv.getAccountId(), pmv.getOpenSocialId(), pmv.getId(), inSessionId);
            }
            // not found in cache or DB, go to ldap to create person entry and principal if possible.
            else
            {
                logger.info("Unable to populate principal from cache/db, looking up user in LDAP for: " + inAccountId);
                Person person = (Person) serviceActionController.execute(new ServiceActionContext(inAccountId, null),
                        createUserfromLdapAction);
                result = new DefaultPrincipal(person.getAccountId(), person.getOpenSocialId(), person.getId(),
                        inSessionId);
            }
        }
        catch (Exception ex)
        {
            logger.info("Unable to populate principal from DataStore for: " + inAccountId, ex);
        }
        return result;
    }

}
