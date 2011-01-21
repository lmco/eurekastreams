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
package org.eurekastreams.server.action.principal;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.exceptions.PrincipalPopulationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populates a principal object based off OpenSocialId.
 * 
 */
public class OpenSocialPrincipalPopulator implements PrincipalPopulator
{
    /**
     * Logger instance.
     */
    Log logger = LogFactory.make();

    /**
     * Person Mapper.
     */
    private GetPeopleByOpenSocialIds personMapper;

    /**
     * Mapper to get a PersonModelView by accountId.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            The person mapper.
     * @param inGetPersonModelViewByAccountIdMapper
     *            Mapper to get a PersonModelView by account id
     */
    public OpenSocialPrincipalPopulator(final GetPeopleByOpenSocialIds inPersonMapper,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper)
    {
        personMapper = inPersonMapper;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
    }

    /**
     * Get Sessionless Principal.
     * 
     * @param inAccountId
     *            the account id.
     * @return the principal.
     */
    public Principal getPrincipal(final String inAccountId)
    {
        return getPrincipal(inAccountId, "");
    }

    /**
     * Retrieve the principal object associated with the OpenSocial id passed in.
     * 
     * @param inOpenSocialId
     *            - string opensocial id to retrieve a principal object for.
     * @param inSessionId
     *            the session ID.
     * @return {@link Principal} object based on the OpenSocial id passed in.
     */
    @Override
    public Principal getPrincipal(final String inOpenSocialId, final String inSessionId)
    {
        PersonModelView user = null;
        try
        {
            // get current user to build principal.
            user = personMapper.fetchUniqueResult(inOpenSocialId);
        }
        catch (Exception e)
        {
            logger.error("Unable to populate principal for OpenSocialId: " + inOpenSocialId);
            throw new PrincipalPopulationException("Unable to populate principal for OpenSocialId: " + inOpenSocialId,
                    e);
        }

        // must handle successful null result from personMapper.fetchUniqueResult
        if (user == null)
        {
            logger.error("Unable to find principal for OpenSocialId: " + inOpenSocialId + " attempting ntid");
            try
            {
                user = getPersonModelViewByAccountIdMapper.execute(inOpenSocialId);
            }
            catch (Exception e)
            {
                throw new PrincipalPopulationException("Unable to find principal for OpenSocialId: " + inOpenSocialId);
            }
            // If the user is still null, neither a valid opensocial id nor a valid ntid was provided.
            if (user == null)
            {
                throw new PrincipalPopulationException("Unable to find principal for OpenSocialId: " + inOpenSocialId);
            }
        }

        return new DefaultPrincipal(user.getAccountId(), user.getOpenSocialId(), user.getEntityId());
    }
}
