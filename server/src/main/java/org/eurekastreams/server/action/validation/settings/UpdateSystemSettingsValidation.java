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
package org.eurekastreams.server.action.validation.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Validate UpdateSystemSettingsExecution input.
 * 
 */
public class UpdateSystemSettingsValidation implements ValidationStrategy<ActionContext>
{
    // TODO: When reactoring, consider using MapParameterValidator so keys and messages are
    // pushed out into config file rather than hard coded.

    /**
     * Last part of a message for too many characters.
     */
    public static final String INPUT_TOO_LONG_MESSAGE = " supports up to " + SystemSettings.MAX_INPUT + " characters";

    /**
     * Site Label Required Error Message.
     */
    public static final String SITE_LABEL_REQUIRED_ERROR_MESSAGE = "Site Label is required";

    /**
     * Site Label Length Error Message.
     */
    public static final String SITE_LABEL_LENGTH_ERROR_MESSAGE = "Site label supports up to "
            + SystemSettings.MAX_SITELABEL_INPUT + " characters";

    /**
     * Plugin Configuration Warning Required Error Message.
     */
    public static final String PLUGIN_WARNING_REQUIRED_ERROR_MESSAGE = "Plugin Configuration Warning is required";

    /**
     * Terms of Service Required Error Message.
     */
    public static final String TOS_REQUIRED_ERROR_MESSAGE = "Terms of Service is required";

    /**
     * Terms of Service Prompt Interval Invalid Input Error Message.
     */
    public static final String TOS_PROMPT_INTERVAL_INVALID_ERROR_MESSAGE = "Prompt Interval for Terms of "
            + "Service supports up to 5 numeric characters";

    /**
     * Terms of Service Prompt Interval Minimal Value Error Message.
     */
    public static final String MIN_TOS_PROMPT_INTERVAL_ERROR_MESSAGE = "Prompt Interval for Terms of "
            + "Service must be greater than " + (SystemSettings.MIN_TOS_PROMPT_INTERVAL - 1);

    /**
     * Content Warning Required Error Message.
     */
    public static final String CONTENT_WARNING_REQUIRED_ERROR_MESSAGE = "Content Warning is required";

    /**
     * Content Warning Length Error Message.
     */
    public static final String CONTENT_WARNING_LENGTH_ERROR_MESSAGE = "Content Warning" + INPUT_TOO_LONG_MESSAGE;

    /**
     * Content Expiration Required Error Message.
     */
    public static final String CONTENT_EXPIRATION_REQUIRED_ERROR_MESSAGE = "Activity Expiration is required";

    /**
     * Content Expiration Value Error Message.
     */
    public static final String CONTENT_EXPIRATION_ERROR_MESSAGE = "Activity Expiration must be a number between "
            + (SystemSettings.MIN_CONTENT_EXPIRATION + 1) + " and " + SystemSettings.MAX_CONTENT_EXPIRATION;

    /**
     * Support Group Short Name Invalid Error Message.
     */
    public static final String SUPPORT_GROUP_NAME_INVALID_ERROR_MESSAGE = "Invalid support group";

    /**
     * Support Phone Number Length Error Message.
     */
    public static final String MAX_SUPPORT_PHONE_NUMBER_LENGTH_ERROR_MESSAGE = "Support Phone Number must be between "
            + "1 and " + SystemSettings.MAX_SUPPORT_PHONE_NUMBER_LENGTH + " characters";

    /**
     * Support Email Address Length Error Message.
     */
    public static final String MAX_SUPPORT_EMAIL_ADDRESS_LENGTH_ERROR_MESSAGE = "Support Email Address supports up to "
            + SystemSettings.MAX_SUPPORT_EMAIL_ADDRESS_LENGTH + " characters";

    /**
     * Support Email Address Invalid Format Error Message.
     */
    public static final String SUPPORT_EMAIL_ADDRESS_INVALID_ERROR_MESSAGE = "Support Email Address is invalid";

    /**
     * System administrators missing error message.
     */
    public static final String SYSTEM_ADMINISTRATORS_EMPTY_ERROR_MESSAGE = "At least one System "
            + "Administrator required.";

    /**
     * At least one of the system admins is locked error message.
     */
    public static final String SYSTEM_ADMINISTRATOR_LOCKED_OUT_ERROR_MESSAGE = // 
    "At least one of the requested administrators is currently locked out of the system: ";
    /**
     * 
     * At least one of the system admins is locked error message.
     */
    public static final String SYSTEM_ADMINISTRATOR_NOTFOUND_ERROR_MESSAGE = // 
    "At least one of the requested administrators is not found in the system: ";

    /**
     * mapper to get group by short name.
     */
    private GetDomainGroupsByShortNames getGroupsByShortNamesMapper;

    /**
     * Mapper to get people by ids.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> peopleByIdsMapper;

    /**
     * Constructor.
     * 
     * @param inGetGroupsByShortNamesMapper
     *            the mapper to get domain group by short name
     * @param inPeopleByIdsMapper
     *            mapper to get people by ids
     */
    public UpdateSystemSettingsValidation(final GetDomainGroupsByShortNames inGetGroupsByShortNamesMapper,
            final DomainMapper<List<Long>, List<PersonModelView>> inPeopleByIdsMapper)
    {
        getGroupsByShortNamesMapper = inGetGroupsByShortNamesMapper;
        peopleByIdsMapper = inPeopleByIdsMapper;
    }

    /**
     * Validate UpdateSystemSettingsExecution input.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @throws ValidationException
     *             If input is invalid.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        /*
         * This is hacky. We are using a unexpected but legal value as our case for if a field is required but not
         * filled in. For String based fields we are using a return of null to signify they are required and are blank
         * For Integer based ones we are returning false.
         * 
         * This is mainly hacky since we know on the front end they are in valid inputs but we send a cryptic message to
         * the backend (see above) that it in turn alerts the user that it is invalid input. But with out a very decent
         * sized refactoring this is the best way.
         */

        // TODO: refactor to get better validation.
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();

        if (fields == null)
        {
            throw new ValidationException("UpdateSystemSettings requires Map of form data");
        }

        ValidationException ve = new ValidationException();

        if (fields.containsKey("ldapGroups") && ((List<MembershipCriteria>) fields.get("ldapGroups")).size() == 0)
        {
            ve.addError("ldapGroups", "At least one entry is required in the access list");
        }

        if (fields.containsKey("contentWarningText") && fields.get("contentWarningText") == null)
        {
            ve.addError("contentWarningText", CONTENT_WARNING_REQUIRED_ERROR_MESSAGE);
        }
        else if (fields.containsKey("contentWarningText")
                && ((String) fields.get("contentWarningText")).length() > SystemSettings.MAX_INPUT)
        {
            ve.addError("contentWarningText", CONTENT_WARNING_LENGTH_ERROR_MESSAGE);
        }

        if (fields.containsKey("siteLabel") && fields.get("siteLabel") == null)
        {
            ve.addError("siteLabel", SITE_LABEL_REQUIRED_ERROR_MESSAGE);
        }
        else if (fields.containsKey("siteLabel")
                && ((String) fields.get("siteLabel")).length() > SystemSettings.MAX_SITELABEL_INPUT)
        {
            ve.addError("siteLabel", SITE_LABEL_LENGTH_ERROR_MESSAGE);
        }

        if (fields.containsKey("termsOfService") && fields.get("termsOfService") == null)
        {
            ve.addError("termsOfService", TOS_REQUIRED_ERROR_MESSAGE);
        }

        if (fields.containsKey("pluginWarning") && fields.get("pluginWarning") == null)
        {
            ve.addError("pluginWarning", PLUGIN_WARNING_REQUIRED_ERROR_MESSAGE);
        }

        if (fields.containsKey("contentExpiration") && fields.get("contentExpiration") == null)
        {
            ve.addError("contentExpiration", CONTENT_EXPIRATION_REQUIRED_ERROR_MESSAGE);
        }
        else if (fields.containsKey("contentExpiration") && !(fields.get("contentExpiration") instanceof Integer))
        {
            ve.addError("contentExpiration", CONTENT_EXPIRATION_ERROR_MESSAGE);
        }
        else if (fields.containsKey("contentExpiration") && fields.get("contentExpiration") != null
                && fields.get("contentExpiration") instanceof Integer
                && ((Integer) fields.get("contentExpiration") < SystemSettings.MIN_CONTENT_EXPIRATION //
                || (Integer) fields.get("contentExpiration") > SystemSettings.MAX_CONTENT_EXPIRATION))
        {
            ve.addError("contentExpiration", CONTENT_EXPIRATION_ERROR_MESSAGE);
        }

        if (fields.containsKey("tosPromptInterval") && !(fields.get("tosPromptInterval") instanceof Integer))
        {
            ve.addError("tosPromptInterval", TOS_PROMPT_INTERVAL_INVALID_ERROR_MESSAGE);
        }
        else if (fields.containsKey("tosPromptInterval")
                && (Integer) fields.get("tosPromptInterval") < SystemSettings.MIN_TOS_PROMPT_INTERVAL)
        {
            ve.addError("tosPromptInterval", MIN_TOS_PROMPT_INTERVAL_ERROR_MESSAGE);
        }

        if (fields.containsKey("supportStreamGroupShortName") && fields.get("supportStreamGroupShortName") != null
                && ((String) fields.get("supportStreamGroupShortName")).length() > 0)
        {
            // short name was passed in - make sure it exists
            if (getGroupsByShortNamesMapper.fetchUniqueResult(
            // line break
                    (String) fields.get("supportStreamGroupShortName")) == null)
            {
                ve.addError("supportStreamGroupShortName", SUPPORT_GROUP_NAME_INVALID_ERROR_MESSAGE);
            }
        }

        if (fields.containsKey("supportPhoneNumber") && fields.get("supportPhoneNumber") != null
                && ((String) fields.get("supportPhoneNumber")).length() > //
                SystemSettings.MAX_SUPPORT_PHONE_NUMBER_LENGTH)
        {
            ve.addError("supportPhoneNumber", MAX_SUPPORT_PHONE_NUMBER_LENGTH_ERROR_MESSAGE);
        }

        if (fields.containsKey("supportEmailAddress") && fields.get("supportEmailAddress") != null
                && ((String) fields.get("supportEmailAddress")).length() > //
                SystemSettings.MAX_SUPPORT_EMAIL_ADDRESS_LENGTH)
        {
            ve.addError("supportEmailAddress", MAX_SUPPORT_EMAIL_ADDRESS_LENGTH_ERROR_MESSAGE);
        }
        else if (fields.containsKey("supportEmailAddress") && fields.get("supportEmailAddress") != null
                && ((String) fields.get("supportEmailAddress")).length() > 0
                && !isValidEmailAddress(((String) fields.get("supportEmailAddress"))))
        {
            ve.addError("supportEmailAddress", SUPPORT_EMAIL_ADDRESS_INVALID_ERROR_MESSAGE);
        }

        if (!fields.containsKey("admins") || fields.get("admins") == null
                || ((HashSet<Person>) fields.get("admins")).size() == 0)
        {
            ve.addError("admins", SYSTEM_ADMINISTRATORS_EMPTY_ERROR_MESSAGE);
        }
        else
        {
            boolean adminErrorOccurred = false;
            // see if the people exist
            HashSet<Person> requestedAdmins = (HashSet<Person>) fields.get("admins");
            List<Long> adminIds = new ArrayList<Long>();

            // convert the list of people to people ids
            for (Person person : requestedAdmins)
            {
                adminIds.add(person.getId());
            }
            // get the people from db/cache
            List<PersonModelView> foundPeople = peopleByIdsMapper.execute(adminIds);

            // check for locked users
            String lockedUsers = "";
            for (PersonModelView foundPerson : foundPeople)
            {
                if (foundPerson.isAccountLocked())
                {
                    if (lockedUsers.length() > 0)
                    {
                        lockedUsers += ", ";
                    }
                    lockedUsers += foundPerson.getAccountId();
                }
            }
            if (lockedUsers.length() > 0)
            {
                // some of the users are locked users
                ve.addError("admins", SYSTEM_ADMINISTRATOR_LOCKED_OUT_ERROR_MESSAGE + lockedUsers);
                adminErrorOccurred = true;
            }

            if (!adminErrorOccurred)
            {
                // check for missing users
                String missingUsers = "";
                for (Person requestedAdmin : requestedAdmins)
                {
                    if (!isPersonIdInPersonList(foundPeople, requestedAdmin.getId()))
                    {
                        // missing person
                        if (missingUsers.length() > 0)
                        {
                            missingUsers += ", ";
                        }
                        missingUsers += requestedAdmin.getAccountId();
                    }
                }
                if (missingUsers.length() > 0)
                {
                    // some of the users weren't found
                    ve.addError("admins", SYSTEM_ADMINISTRATOR_NOTFOUND_ERROR_MESSAGE + missingUsers);
                }
            }
        }

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }
    }

    /**
     * Check whether the input person account id is found in the list of people.
     * 
     * @param inPeople
     *            the list of people to look through
     * @param inPersonId
     *            the person id to search for
     * @return whether the person id is found in the input person list
     */
    private boolean isPersonIdInPersonList(final List<PersonModelView> inPeople, final Long inPersonId)
    {
        for (PersonModelView person : inPeople)
        {
            if (person.getId() == inPersonId)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param inEmailToTest
     *            email to test.
     * @return if email is valid.
     */
    private boolean isValidEmailAddress(final String inEmailToTest)
    {
        boolean result = true;

        try
        {
            InternetAddress emailAddr = new InternetAddress(inEmailToTest);
            if (!fullAddress(inEmailToTest))
            {
                result = false;
            }
        }
        catch (AddressException ex)
        {
            result = false;
        }
        return result;
    }

    /**
     * @param inEmailAddress
     *            email to test.
     * @return checks to see if email has a domain.
     */
    private boolean fullAddress(final String inEmailAddress)
    {
        String[] addressTokens = inEmailAddress.split("@");
        return addressTokens.length == 2;
    }
}
