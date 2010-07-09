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
import org.eurekastreams.server.domain.CompositeEntity;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * 
 * Validates values entered for a Organization.
 * 
 */
public class UpdateOrganizationValidation implements ValidationStrategy<ActionContext>
{
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

        vHelper.getAndCheckStringFieldExist(fields, OrganizationModelView.ID_KEY, true, ve);
        vHelper.getAndCheckStringFieldExist(fields, OrganizationModelView.SHORT_NAME_KEY, true, ve);

        String url = (String) vHelper.getAndCheckStringFieldExist(fields, OrganizationModelView.URL_KEY, false, ve);
        vHelper.stringMeetsRequirments(OrganizationModelView.URL_KEY, url, ve, null, null, null,
                CompositeEntity.URL_REGEX_PATTERN, Organization.WEBSITE_MESSAGE);

        Set coordinators = (Set) vHelper.getAndCheckStringFieldExist(fields, OrganizationModelView.COORDINATORS_KEY,
                true, ve);
        if (coordinators == null || coordinators.isEmpty())
        {
            ve.addError(OrganizationModelView.COORDINATORS_KEY, Organization.MIN_COORDINATORS_MESSAGE);
        }

        String name = (String) vHelper.getAndCheckStringFieldExist(fields, OrganizationModelView.NAME_KEY, true, ve);
        if (name == null || name.isEmpty())
        {
            ve.addError(OrganizationModelView.NAME_KEY, Organization.NAME_REQUIRED);
        }
        else
        {
            vHelper.stringMeetsRequirments(OrganizationModelView.NAME_KEY, name, ve, Organization.NAME_LENGTH_MESSAGE,
                    Organization.MAX_NAME_LENGTH, Organization.NAME_LENGTH_MESSAGE, null, null);
        }

        String description = (String) vHelper.getAndCheckStringFieldExist(fields,
                OrganizationModelView.DESCRIPTION_KEY, true, ve);
        vHelper.stringMeetsRequirments(OrganizationModelView.DESCRIPTION_KEY, description, ve, null,
                Organization.MAX_DESCRIPTION_LENGTH, Organization.DESCRIPTION_LENGTH_MESSAGE, null, null);

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }
    }

}
