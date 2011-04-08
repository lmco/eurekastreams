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
package org.eurekastreams.server.service.restlets;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Used to verify Person/Group shortNames.
 * 
 */
public class StreamIdValidationResource extends SmpResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Request param for stream key to validate.
     */
    private String uniqueKey;

    /**
     * Request param for stream type.
     */
    private EntityType type;

    /**
     * Person model view mapper.
     */
    private final DomainMapper<String, PersonModelView> getPersonMVByAccountId;

    /**
     * Groups by shortName DAO.
     */
    private final GetDomainGroupsByShortNames groupByShortNameDAO;

    /**
     * Instance of the {@link PlatformTransactionManager}.
     */
    private final PlatformTransactionManager transManager;

    /**
     * Constructor.
     * 
     * @param inGetPersonMVByAccountId
     *            Person model view mapper.
     * @param inGroupByShortNameDAO
     *            Groups by shortName DAO.
     * @param inTransManager
     *            - instance of the {@link PlatformTransactionManager}.
     */
    public StreamIdValidationResource(final DomainMapper<String, PersonModelView> inGetPersonMVByAccountId,
            final GetDomainGroupsByShortNames inGroupByShortNameDAO, final PlatformTransactionManager inTransManager)
    {
        getPersonMVByAccountId = inGetPersonMVByAccountId;
        groupByShortNameDAO = inGroupByShortNameDAO;
        transManager = inTransManager;
    }

    /**
     * Initialize parameters from the request object.
     * 
     * @param inRequest
     *            the client's request
     */
    @Override
    protected void initParams(final Request inRequest)
    {
        uniqueKey = (String) inRequest.getAttributes().get("uniqueKey");
        type = EntityType.valueOf(((String) inRequest.getAttributes().get("type")).trim().toUpperCase());
    }

    /**
     * Handle GET requests.
     * 
     * @param variant
     *            the variant to be retrieved.
     * @throws ResourceException
     *             thrown if a representation cannot be provided
     * @return a representation of the resource
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setReadOnly(true);
        TransactionStatus currentStatus = transManager.getTransaction(transDef);

        String response;
        String typeString = type.toString();
        try
        {
            switch (type)
            {
            case PERSON:
                PersonModelView pmv = getPersonMVByAccountId.execute(uniqueKey);
                response = pmv == null ? "INVALID " : "VALID ";
                break;
            case GROUP:
                List<DomainGroupModelView> groups = groupByShortNameDAO.execute(Collections.singletonList(uniqueKey));
                response = groups.size() == 1 ? "VALID " : "INVALID ";
                break;
            default:
                typeString = "TYPE";
                throw new RuntimeException("only accepts person and group types.");
            }
            transManager.commit(currentStatus);
        }
        catch (Exception e)
        {
            log.warn("Error validating id", e);
            transManager.rollback(currentStatus);
            response = "INVALID ";
        }

        Representation rep = new StringRepresentation(response + typeString, MediaType.TEXT_HTML);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

}
