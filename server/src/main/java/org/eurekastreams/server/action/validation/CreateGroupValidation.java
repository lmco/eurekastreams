/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.validation;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * 
 * Validates values entered for a Group.
 * 
 */
public class CreateGroupValidation implements ValidationStrategy<ActionContext>
{

    /**
     * {@link GetDomainGroupsByShortNames}.
     */
    private final GetDomainGroupsByShortNames groupMapper;

    /**
     * @param inGroupMapper
     *            the mapper to use to get groups.
     */
    public CreateGroupValidation(final GetDomainGroupsByShortNames inGroupMapper)
    {
        groupMapper = inGroupMapper;
    }

    /**
     * These are one off messages that are only used for this validation if these need to be reused move them to the
     * DTO. These are package protected so that test can access them.
     */

    /**
     * Message to display if name is taken.
     */
    static final String SHORTNAME_TAKEN_MESSAGE = "A group with this web address already exist.";

    /**
     *
     */
    static final String PRIVACY_KEY_MESSAGE = "Privacy key excepted.";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();

        ValidationException ve = new ValidationException();
        ValidationHelper vHelper = new ValidationHelper();

        String groupName = (String) vHelper
                .getAndCheckStringFieldExist(fields, DomainGroupModelView.NAME_KEY, true, ve);
        if (groupName == null || groupName.isEmpty())
        {
            ve.addError(DomainGroupModelView.NAME_KEY, DomainGroup.NAME_REQUIRED);
        }
        else
        {
            vHelper.stringMeetsRequirments(DomainGroupModelView.NAME_KEY, groupName, ve,
                    DomainGroup.NAME_LENGTH_MESSAGE, DomainGroup.MAX_NAME_LENGTH, DomainGroup.NAME_LENGTH_MESSAGE,
                    null, null);
        }

        String groupShortName = (String) vHelper.getAndCheckStringFieldExist(fields,
                DomainGroupModelView.SHORT_NAME_KEY, true, ve);
        if (groupShortName == null || groupShortName.isEmpty())
        {
            ve.addError(DomainGroupModelView.SHORT_NAME_KEY, DomainGroup.SHORTNAME_REQUIRED);
        }
        else if (vHelper.stringMeetsRequirments(DomainGroupModelView.SHORT_NAME_KEY, groupShortName, ve,
                DomainGroup.SHORT_NAME_LENGTH_MESSAGE, DomainGroup.MAX_SHORT_NAME_LENGTH,
                DomainGroup.SHORT_NAME_LENGTH_MESSAGE, DomainGroup.ALPHA_NUMERIC_PATTERN,
                DomainGroup.SHORT_NAME_CHARACTERS))
        {
            if (groupMapper.fetchUniqueResult(groupShortName.toLowerCase()) != null)
            {
                ve.addError(DomainGroupModelView.SHORT_NAME_KEY, SHORTNAME_TAKEN_MESSAGE);
            }
        }

        String groupDesc = (String) vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.DESCRIPTION_KEY,
                true, ve);
        if (groupDesc == null || groupDesc.isEmpty())
        {
            ve.addError(DomainGroupModelView.DESCRIPTION_KEY, DomainGroup.DESCRIPTION_REQUIRED);
        }
        else
        {
            vHelper.stringMeetsRequirments(DomainGroupModelView.DESCRIPTION_KEY, groupDesc, ve,
                    DomainGroup.DESCRIPTION_LENGTH_MESSAGE, DomainGroup.MAX_DESCRIPTION_LENGTH,
                    DomainGroup.DESCRIPTION_LENGTH_MESSAGE, null, null);
        }

        Set coordinators = (Set) vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.COORDINATORS_KEY,
                true, ve);
        if (coordinators == null || coordinators.isEmpty())
        {
            ve.addError(DomainGroupModelView.COORDINATORS_KEY, DomainGroup.MIN_COORDINATORS_MESSAGE);
        }

        vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.PRIVACY_KEY, true, ve);

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }
    }

}
