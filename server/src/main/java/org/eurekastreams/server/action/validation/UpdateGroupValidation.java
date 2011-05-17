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
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * 
 * Validates values entered for a Group.
 * 
 */
public class UpdateGroupValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();

        ValidationException ve = new ValidationException();
        ValidationHelper vHelper = new ValidationHelper();

        vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.ID_KEY, true, ve);
        vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.SHORT_NAME_KEY, true, ve);

        if (fields.containsKey(DomainGroupModelView.PRIVACY_KEY))
        {
            throw new ValidationException(ValidationHelper.UNEXPECTED_DATA_ERROR_MESSAGE);
        }

        Set coordinators = (Set) vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.COORDINATORS_KEY,
                true, ve);
        if (coordinators == null || coordinators.isEmpty())
        {
            ve.addError(DomainGroupModelView.COORDINATORS_KEY, DomainGroup.MIN_COORDINATORS_MESSAGE);
        }

        String name = (String) vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.NAME_KEY, true, ve);
        if (name == null || name.isEmpty())
        {
            ve.addError(DomainGroupModelView.NAME_KEY, DomainGroup.NAME_REQUIRED);
        }
        else
        {
            vHelper.stringMeetsRequirments(DomainGroupModelView.NAME_KEY, name, ve, DomainGroup.NAME_LENGTH_MESSAGE,
                    DomainGroup.MAX_NAME_LENGTH, DomainGroup.NAME_LENGTH_MESSAGE, null, null);
        }

        String description = (String) vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.DESCRIPTION_KEY,
                true, ve);
        vHelper.stringMeetsRequirments(DomainGroupModelView.DESCRIPTION_KEY, description, ve, null,
                DomainGroup.MAX_DESCRIPTION_LENGTH, DomainGroup.DESCRIPTION_LENGTH_MESSAGE, null, null);

        String keywords = (String) vHelper.getAndCheckStringFieldExist(fields, DomainGroupModelView.KEYWORDS_KEY, true,
                ve);

        if (keywords != null)
        {
            if (!vHelper.validBackgroundItems(keywords))
            {
                ve.addError(DomainGroupModelView.KEYWORDS_KEY, DomainGroupModelView.KEYWORD_MESSAGE);
            }
        }

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }
    }

}
