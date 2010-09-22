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
package org.eurekastreams.server.action.validation.profile;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetRelatedEntityCountRequest;

/**
 * Validator to check if organization can be deleted.
 * 
 */
public class DeleteOrganizationValidation implements ValidationStrategy<ActionContext>
{
    /**
     * Logger.
     */
    Log log = LogFactory.make();

    /**
     * Mapper to find counts for related objects.
     */
    private DomainMapper<GetRelatedEntityCountRequest, Long> relatedEntityCountMapper;

    /**
     * Constructor.
     * 
     * @param inRelatedEntityCountMapper
     *            Mapper to find counts for related objects.
     */
    public DeleteOrganizationValidation(
            final DomainMapper<GetRelatedEntityCountRequest, Long> inRelatedEntityCountMapper)
    {
        relatedEntityCountMapper = inRelatedEntityCountMapper;
    }

    /**
     * Check and make sure that Org is not root org and has no sub-orgs, sub-groups, activities.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @throws ValidationException
     *             If organization does not meet all criteria for deletion.
     */
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        Long orgId = (Long) inActionContext.getParams();
        Long group = relatedEntityCountMapper.execute(new GetRelatedEntityCountRequest("DomainGroup",
                "parentOrganization", orgId));
        Long org = relatedEntityCountMapper.execute(new GetRelatedEntityCountRequest("Organization",
                "parentOrganization", orgId));
        Long activity = relatedEntityCountMapper.execute(new GetRelatedEntityCountRequest("Activity",
                "recipientParentOrg", orgId));

        if ((group + org + activity) != 0)
        {
            throw new ValidationException("Unable to delete organization. Related items still exist. Groups:" + group
                    + " Orgs:" + org + " Activity:" + activity);
        }
    }
}
