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
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Validation for updating person.
 *
 */
public class UpdatePersonValidation implements ValidationStrategy<ServiceActionContext>
{

    /**
     * Use when no length is provided by the Domain Object.
     *
     * This is package protected for use in testing.
     */
    static final int DEFAULT_MAX_STRING_LENGTH = 255;

    /**
     * These messages are one off messages only used in this validation.
     *
     * they are package protected for verification in tests.
     *
     */
    /**
     * email length message.
     */
    static final String EMAIL_LENGTH_MESSAGE = "Email Adresses should be less then " + DEFAULT_MAX_STRING_LENGTH
            + " charecters.";

    /**
     * perferred name length message.
     */
    static final String PREFERREDNAME_MESSAGE = "Display Name is required.";

    /**
     * {@link EmailAddressValidator}.
     */
    private final EmailAddressValidator emailValidator;

    /**
     * @param inEmailValidator
     *            the validator to check the email against.
     */
    public UpdatePersonValidation(final EmailAddressValidator inEmailValidator)
    {
        emailValidator = inEmailValidator;
    }

    /**
     *
     * @param inActionContext
     *            context for the action.
     * @throws ValidationException
     *             if there is a validation problem.
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public void validate(final ServiceActionContext inActionContext) throws ValidationException
    {
        HashMap<String, Serializable> personData = (HashMap<String, Serializable>) inActionContext.getParams();

        ValidationHelper vHelper = new ValidationHelper();
        ValidationException ve = new ValidationException();

        String title = (String) vHelper.getAndCheckStringFieldExist(personData, PersonModelView.TITILE_KEY, true, ve);
        vHelper.stringMeetsRequirments(PersonModelView.TITILE_KEY, title, ve, "Title is required.",
                Person.MAX_TITLE_LENGTH, Person.TITLE_MESSAGE, null, null);

        String perferredName = (String) vHelper.getAndCheckStringFieldExist(personData,
                PersonModelView.PREFERREDNAME_KEY, true, ve);
        vHelper.stringMeetsRequirments(PersonModelView.PREFERREDNAME_KEY, perferredName, ve, PREFERREDNAME_MESSAGE,
                +DEFAULT_MAX_STRING_LENGTH, PREFERREDNAME_MESSAGE, "^[a-zA-Z\\'\\`\\ \\-]+$",
                "Display Name has invalid characters.");

        String description = (String) vHelper.getAndCheckStringFieldExist(personData, PersonModelView.DESCRIPTION_KEY,
                true, ve);
        vHelper.stringMeetsRequirments(PersonModelView.DESCRIPTION_KEY, description, ve, null,
                Person.MAX_JOB_DESCRIPTION_LENGTH, Person.JOB_DESCRIPTION_MESSAGE, null, null);

        String workNumber = (String) vHelper.getAndCheckStringFieldExist(personData, PersonModelView.WORKPHONE_KEY,
                true, ve);
        vHelper.stringMeetsRequirments(PersonModelView.WORKPHONE_KEY, workNumber, ve, null,
                Person.MAX_PHONE_NUMBER_LENGTH, Person.PHONE_NUMBER_MESSAGE, null, null);

        // TODO: Commented out to fix person update for 1.5, but future of phone numbers is uncertain right now as spec
        // shows us
        // collecting a number, but not displaying it,
        // Also, prod db shows more people have entered fax numbers than either of the other two numbers (which is
        // weird)
        // so we may want to wait before dropping all that data.
        // will submit to Lisa for clarification before deleteing this fuctionality all the way through the entity/db,
        // losing
        // a lot of data.

        // String cellNumber = (String) vHelper.getAndCheckStringFieldExist(personData, PersonModelView.CELLPHONE_KEY,
        // true, ve);
        // vHelper.stringMeetsRequirments(PersonModelView.CELLPHONE_KEY, cellNumber, ve, null,
        // Person.MAX_PHONE_NUMBER_LENGTH, Person.PHONE_NUMBER_MESSAGE, null, null);
        //
        // String faxNumber = (String) vHelper.getAndCheckStringFieldExist(personData, PersonModelView.FAX_KEY, true,
        // ve);
        // vHelper.stringMeetsRequirments(PersonModelView.FAX_KEY, faxNumber, ve, null, Person.MAX_PHONE_NUMBER_LENGTH,
        // Person.FAX_NUMBER_MESSAGE, null, null);

        String email = (String) vHelper.getAndCheckStringFieldExist(personData, PersonModelView.EMAIL_KEY, true, ve);
        if (vHelper.stringMeetsRequirments(PersonModelView.EMAIL_KEY, email, ve, null, DEFAULT_MAX_STRING_LENGTH,
                EMAIL_LENGTH_MESSAGE, null, null) && StringUtils.isNotBlank(email))
        {
            try
            {
                emailValidator.validate(email);
            }
            catch (ValidationException ex)
            {
                ve.addError(PersonModelView.EMAIL_KEY, ex.getMessage());
            }
        }

        String skills = (String) vHelper.getAndCheckStringFieldExist(personData, PersonModelView.SKILLS_KEY, true, ve);
        if (skills != null)
        {
            if (!vHelper.validBackgroundItems(skills))
            {
                ve.addError(PersonModelView.SKILLS_KEY, PersonModelView.SKILLS_MESSAGE);
            }
        }

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }

    }
}
