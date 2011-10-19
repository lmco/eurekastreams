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
package org.eurekastreams.server.service.restlets.support;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.restlet.data.Form;
import org.restlet.data.Request;

/**
 * Use input request to get user principal.
 */
public class OAuthRequestToPrincipalTransformer implements Transformer<Request, Principal>
{
    /** Logger. */
    private static Log log = LogFactory.make();

    /** Principal from account ID DAO. */
    private final DomainMapper<String, Principal> accountIdPrincipalDao;

    /** Principal from OpenSocial ID DAO. */
    private final DomainMapper<String, Principal> openSocialIdPrincipalDao;

    /**
     * Constructor.
     *
     * @param inAccountIdPrincipalDao
     *            Principal from account ID DAO.
     * @param inOpenSocialIdPrincipalDao
     *            Principal from OpenSocial ID DAO.
     */
    public OAuthRequestToPrincipalTransformer(final DomainMapper<String, Principal> inAccountIdPrincipalDao,
            final DomainMapper<String, Principal> inOpenSocialIdPrincipalDao)
    {
        accountIdPrincipalDao = inAccountIdPrincipalDao;
        openSocialIdPrincipalDao = inOpenSocialIdPrincipalDao;
    }

    /**
     * Use input request to get user principal.
     *
     * @param inRequest
     *            Request.
     * @return User principal.
     */
    @Override
    public Principal transform(final Request inRequest)
    {
        String accountid = null;
        Principal result = null;

        if (inRequest.getAttributes().containsKey("org.restlet.http.headers"))
        {
            Form httpHeaders = (Form) inRequest.getAttributes().get("org.restlet.http.headers");

            if (httpHeaders.getFirstValue("accountid") != null)
            {
                log.debug("Found accountid header: " + accountid);
                accountid = httpHeaders.getFirstValue("accountid");
            }
        }

        // If header doesn't exist fall back on opensocial param populated by shindig.
        if (accountid != null)
        {
            result = accountIdPrincipalDao.execute(accountid);
        }
        else
        {
            String osId = inRequest.getOriginalRef().getQueryAsForm().getFirstValue("opensocial_viewer_id");
            if (osId != null)
            {
                result = openSocialIdPrincipalDao.execute(osId);
            }
        }

        return result;
    }
}
