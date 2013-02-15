/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.ldap.callback;

import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.springframework.ldap.core.AttributesMapper;

/**
 * Translates a LDAP record to a Person object.
 */
public class LdapToPersonMapper implements AttributesMapper
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(LdapToPersonMapper.class);

    /**
     * User account attribute name.
     */
    private String accountAttrib;

    /**
     * User first name attribute name.
     */
    private String firstNameAttrib;

    /**
     * User last name attribute name.
     */
    private String lastNameAttrib;

    /**
     * User middle name attribute name.
     */
    private String middleNameAttrib;

    /**
     * User title attribute name.
     */
    private String titleAttrib;

    /**
     * User full name attribute name.
     */
    private String fullNameAttrib;

    /**
     * User email attribute name.
     */
    private String emailAttrib;

    /**
     * Company attribute.
     */
    private String companyAttrib;

    /**
     * Additional properties to load.
     */
    private List<String> additionalProperties;

    /**
     * Transformer to convert the Attributes to a display name suffix (optional).
     */
    private Transformer<Attributes, String> attributesToDisplayNameSuffixTransformer;

    /**
     * Maps the LDAP attributes to a Person.
     * 
     * @param attrs
     *            the attributes.
     * @throws NamingException
     *             on issue.
     * @return a person object.
     */
    public Object mapFromAttributes(final Attributes attrs) throws NamingException
    {
        Person person = null;

        try
        {
            log.debug("Found person: " + attrs.get(fullNameAttrib).get().toString());

            String accountId = attrs.get(accountAttrib).get().toString();
            String firstName = attrs.get(firstNameAttrib).get().toString();
            String lastName = attrs.get(lastNameAttrib).get().toString();

            String email = (null != attrs.get(emailAttrib)) ? attrs.get(emailAttrib).get().toString() : "";

            String middleName = (null != attrs.get(middleNameAttrib)) ? attrs.get(middleNameAttrib).get().toString()
                    : "";

            String companyName = (null != attrs.get(companyAttrib)) ? attrs.get(companyAttrib).get().toString() : "";

            String title = (null != attrs.get(titleAttrib)) ? attrs.get(titleAttrib).get().toString() : "";

            String[] splitCn = attrs.get(fullNameAttrib).get().toString().split(", ");
            String preferredName = (splitCn.length > 1) ? splitCn[1] : firstName;

            person = new Person(accountId, firstName, middleName, lastName, preferredName);
            log.debug("Company Name:" + companyName);

            person.setCompanyName(companyName);
            person.setTitle(title);
            person.setEmail(email);

            if (attributesToDisplayNameSuffixTransformer != null)
            {
                String displayNameSuffix = attributesToDisplayNameSuffixTransformer.transform(attrs);
                if (displayNameSuffix == null)
                {
                    displayNameSuffix = "";
                }
                person.setDisplayNameSuffix(displayNameSuffix);
                log.debug("Setting the display name suffix: " + displayNameSuffix);
            }

            if (additionalProperties != null)
            {
                HashMap<String, String> propertiesMap = new HashMap<String, String>();
                for (String property : additionalProperties)
                {
                    // Some additional configurated properties may not be
                    // available for all users. Do not
                    // halt on those properties, just move on.
                    try
                    {
                        propertiesMap.put(property, attrs.get(property).get().toString());
                    }
                    catch (Exception ex)
                    {
                        if (log.isInfoEnabled())
                        {
                            log.info("Additional Property: " + property + " not found for user " + accountId);
                        }
                    }
                }
                person.setAdditionalProperties(propertiesMap);
            }
        }
        catch (Exception e)
        {
            log.error("Error instantiating person object.", e);
        }

        return person;
    }

    /**
     * @param inAccountAttrib
     *            the accountAttrib to set.
     */
    public void setAccountAttrib(final String inAccountAttrib)
    {
        accountAttrib = inAccountAttrib;
    }

    /**
     * @param inFirstNameAttrib
     *            the firstNameAttrib to set.
     */
    public void setFirstNameAttrib(final String inFirstNameAttrib)
    {
        firstNameAttrib = inFirstNameAttrib;
    }

    /**
     * @param inLastNameAttrib
     *            the lastNameAttrib to set.
     */
    public void setLastNameAttrib(final String inLastNameAttrib)
    {
        lastNameAttrib = inLastNameAttrib;
    }

    /**
     * @param inMiddleNameAttrib
     *            the middleNameAttrib to set.
     */
    public void setMiddleNameAttrib(final String inMiddleNameAttrib)
    {
        middleNameAttrib = inMiddleNameAttrib;
    }

    /**
     * @param inTitleAttrib
     *            the titleAttrib to set.
     */
    public void setTitleAttrib(final String inTitleAttrib)
    {
        titleAttrib = inTitleAttrib;
    }

    /**
     * @param inFullNameAttrib
     *            the fullNameAttrib to set.
     */
    public void setFullNameAttrib(final String inFullNameAttrib)
    {
        fullNameAttrib = inFullNameAttrib;
    }

    /**
     * @param inEmailAttrib
     *            the emailAttrib to set.
     */
    public void setEmailAttrib(final String inEmailAttrib)
    {
        emailAttrib = inEmailAttrib;
    }

    /**
     * @param inAdditionalProperties
     *            the additionalProperties to set.
     */
    public void setAdditionalProperties(final List<String> inAdditionalProperties)
    {
        additionalProperties = inAdditionalProperties;
    }

    /**
     * @param inCompanyAttrib
     *            the companyAttrib to set
     */
    public void setCompanyAttrib(final String inCompanyAttrib)
    {
        companyAttrib = inCompanyAttrib;
    }

    /**
     * Set the attributes to display name suffix mapper.
     * 
     * @param inAttributesToDisplayNameSuffixTransformer
     *            transformer to create the display name suffix from Attributes
     */
    public void setAttributesToDisplayNameSuffixTransformer(
            final Transformer<Attributes, String> inAttributesToDisplayNameSuffixTransformer)
    {
        attributesToDisplayNameSuffixTransformer = inAttributesToDisplayNameSuffixTransformer;
    }
}
