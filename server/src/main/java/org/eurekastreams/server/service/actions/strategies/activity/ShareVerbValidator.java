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

package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Class that performs the validation for Share activities.
 * 
 */
public class ShareVerbValidator implements ActivityValidator
{
    /**
     * Local instance of the activity mapper.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>>  activityMapper;

    /**
     * Local instance of the TransactionManager to be injected.
     */
    private PlatformTransactionManager transMgr;

    /**
     * Used to lookup groups too see if sharing is allowed (private groups).
     */
    private GetDomainGroupsByShortNames groupShortNameCacheMapper;

    /**
     * Base constructor.
     * 
     * @param inActivityMapper
     *            - mapper for retrieving Activities by id from storage.
     * @param inTransManager
     *            - transaction manager for shared verb validation. This is needed because this validator uses the
     *            BulkActivities Mapper to retrieve the original Activity to be sure that the properties are the same as
     *            the shared Activity.
     * @param inGroupShortNameCacheMapper
     *            The mapper to get a group by short name. needed because private group activities can not be shared.
     */
    public ShareVerbValidator(final DomainMapper<List<Long>, List<ActivityDTO>>  inActivityMapper,
            final PlatformTransactionManager inTransManager,
            final GetDomainGroupsByShortNames inGroupShortNameCacheMapper)
    {
        activityMapper = inActivityMapper;
        transMgr = inTransManager;
        groupShortNameCacheMapper = inGroupShortNameCacheMapper;
    }

    /**
     * Perform the validation to ensure a correctly formed Share Activity. The following criteria are enforced:
     * 
     * - Original Actor must be supplied
     * 
     * - Original Activity must be found in the db and only one occurrence of Activity Id.
     * 
     * - Original Activity BaseObjectProperties must match the new Activity's BaseObjectProperties.
     * 
     * - Original Actor of the Shared Activity must match the Actor of the Original Activity.
     * 
     * - BaseObjectType of the shared activity must match the BaseObjectType of the original activity.
     * 
     * - Original Activity must not be from a Private Group.
     * 
     * @param inActivity
     *            - activityDTO instance to validate.
     */
    public void validate(final ActivityDTO inActivity)
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("LookupActivityTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        ValidationException ve = new ValidationException();

        // Actor is not validated because it is supplied by the action.

        if (inActivity.getOriginalActor() == null || inActivity.getOriginalActor().getUniqueIdentifier() == null
                || inActivity.getOriginalActor().getUniqueIdentifier().length() <= 0)
        {
            ve.addError("OriginalActor", "Must be included for Share verbs.");
            throw ve;
        }

        List<Long> activityIds = new ArrayList<Long>(1);
        activityIds.add(new Long(inActivity.getBaseObjectProperties().get("originalActivityId")));
        inActivity.getBaseObjectProperties().remove("originalActivityId");
        List<ActivityDTO> originalActivityResults;
        try
        {
            originalActivityResults = activityMapper.execute(activityIds);

            // If the original activity cannot be found throw an error right away.
            if (originalActivityResults.size() == 0)
            {
                ve.addError("OriginalActivity", "activity being shared could not be found in the db.");
                throw ve;
            }

            // if a unuqie activity is not found throw an error.
            if (originalActivityResults.size() > 1)
            {
                ve.addError("OriginalActivity", "more than one result was found for the original activity id.");
            }

            ActivityDTO origActivity = originalActivityResults.get(0);

            if (origActivity.getDestinationStream().getType() == EntityType.GROUP)
            {
                DomainGroupModelView group = groupShortNameCacheMapper.fetchUniqueResult(origActivity
                        .getDestinationStream().getUniqueIdentifier());

                if (group != null && !group.isPublic())
                {
                    ve.addError("OriginalActivity", "OriginalActivity from a private group and can not be shared.");
                    throw ve;
                }
                else if (group == null)
                {
                    ve.addError("OriginalActivity", "OriginalActivity Group Entity not found.");
                }
            }

            if (!inActivity.getBaseObjectProperties().equals(origActivity.getBaseObjectProperties()))
            {
                ve.addError("BaseObjectProperties",
                        "Oringal Activity BaseObjectProperties must equal the properties of the Shared Activity.");
            }
            else
            {
                // Push the property back onto the object properties for use
                // in storing the original id.
                inActivity.getBaseObjectProperties().put("originalActivityId", activityIds.get(0).toString());
            }

            if (!inActivity.getOriginalActor().getUniqueIdentifier().equals(
                    origActivity.getActor().getUniqueIdentifier()))
            {
                ve.addError("OriginalActor",
                        "Original actor of the shared activity does not match the actor of the original activity.");
            }

            if (!inActivity.getBaseObjectType().equals(origActivity.getBaseObjectType()))
            {
                ve.addError("BaseObjectType", "activity must be of the same type as the original activity.");
            }

            transMgr.commit(transStatus);
        }
        catch (Exception ex)
        {
            transMgr.rollback(transStatus);
            ve.addError("OriginalActivity", "Error occurred accessing the original activity.");
        }

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }
    }

}
