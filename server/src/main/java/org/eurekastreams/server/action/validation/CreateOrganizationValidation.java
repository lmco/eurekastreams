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
package org.eurekastreams.server.action.validation;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 *
 * Validates values entered for a orgs.
 *
 */
public class CreateOrganizationValidation implements ValidationStrategy<ActionContext>
{

    /**
     * {@link GetOrganizationsByShortNames}.
     */
    private GetOrganizationsByShortNames mapper;

    /**
     * @param inMapper
     *            the mapper to use to get orgs.
     */
    public CreateOrganizationValidation(final GetOrganizationsByShortNames inMapper)
    {
        mapper = inMapper;
    }

    /**
     * These are one off messages that are only used for this validation if these need to be reused move them to the
     * DTO. These are package protected so that test can access them.
     */

    /**
     * Message to display if name is taken.
     */
    static final String SHORTNAME_TAKEN_MESSAGE = "A organization with this web address already exist.";

    /**
     * message if no such parent org exist.
     */
    static final String NO_SUCH_PARENT_ORG = "The selected parent organization does not exist.";

    /**
     * message if no parent org is selected.
     */
    static final String MUST_HAVE_PARENT_ORG_MESSAGE = "Please select a parent organization.";

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

        String orgName = (String) vHelper.getAndCheckStringFieldExist(fields, OrganizationModelView.NAME_KEY, true, ve);
        if (orgName == null || orgName.isEmpty())
        {
            ve.addError(OrganizationModelView.NAME_KEY, Organization.NAME_REQUIRED);
        }
        else
        {
            vHelper.stringMeetsRequirments(OrganizationModelView.NAME_KEY, orgName, ve,
                    Organization.NAME_LENGTH_MESSAGE, Organization.MAX_NAME_LENGTH, Organization.NAME_LENGTH_MESSAGE,
                    null, null);
        }

        String orgShortName = (String) vHelper.getAndCheckStringFieldExist(fields,
                OrganizationModelView.SHORT_NAME_KEY, true, ve);
        if (orgShortName == null || orgShortName.isEmpty())
        {
            ve.addError(OrganizationModelView.SHORT_NAME_KEY, Organization.SHORTNAME_REQUIRED);
        }
        else if (vHelper.stringMeetsRequirments(OrganizationModelView.SHORT_NAME_KEY, orgShortName, ve,
                Organization.SHORT_NAME_LENGTH_MESSAGE, Organization.MAX_NAME_LENGTH,
                Organization.SHORT_NAME_LENGTH_MESSAGE, Organization.ALPHA_NUMERIC_PATTERN,
                Organization.SHORT_NAME_CHARACTERS))
        {
            if (mapper.fetchUniqueResult(orgShortName.toLowerCase()) != null)
            {
                ve.addError(OrganizationModelView.SHORT_NAME_KEY, SHORTNAME_TAKEN_MESSAGE);
            }
        }

        String parentOrgShortName = (String) vHelper.getAndCheckStringFieldExist(fields,
                OrganizationModelView.ORG_PARENT_KEY, true, ve);
        if (vHelper.stringMeetsRequirments(OrganizationModelView.ORG_PARENT_KEY, parentOrgShortName, ve,
                MUST_HAVE_PARENT_ORG_MESSAGE, null, null, null, null))
        {
            if (mapper.fetchUniqueResult(parentOrgShortName) == null)
            {
                ve.addError(OrganizationModelView.ORG_PARENT_KEY, NO_SUCH_PARENT_ORG);
            }
        }

        Set coordinators = (Set) vHelper.getAndCheckStringFieldExist(fields, OrganizationModelView.COORDINATORS_KEY,
                true, ve);
        if (coordinators == null || coordinators.isEmpty())
        {
            ve.addError(OrganizationModelView.COORDINATORS_KEY, Organization.MIN_COORDINATORS_MESSAGE);
        }

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }
    }

}
