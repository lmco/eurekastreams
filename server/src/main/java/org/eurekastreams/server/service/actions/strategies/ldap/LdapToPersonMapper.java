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
package org.eurekastreams.server.service.actions.strategies.ldap;

import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Translates a LDAP record to a Person object.
 */
public class LdapToPersonMapper implements AttributesMapper, PeopleAppender
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(LdapToPersonMapper.class);

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
     * User org attribute name.
     */
    private String orgAttrib;

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
     * Support email address - used when no email address was found for a user.
     */
    private String supportEmail;
    
    /**
     * Additional properties to load.
     */
    private List<String> additionalProperties;
    
    /**
     * List of people.
     */
    private HashMap<String, Person> people = null;

    /**
     * @param inPeople
     *            the people list.
     */
    public void setPeople(final HashMap<String, Person> inPeople)
    {
        people = inPeople;
    }

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
            if (log.isDebugEnabled())
            {
            	log.debug("Found person: " + attrs.get(fullNameAttrib).get().toString());
            }

            String accountId = attrs.get(accountAttrib).get().toString();
            String firstName = attrs.get(firstNameAttrib).get().toString();
            String lastName = attrs.get(lastNameAttrib).get().toString();

            String email = (null != attrs.get(emailAttrib)) ? attrs.get(emailAttrib).get().toString()
                    : supportEmail;

            String middleName = (null != attrs.get(middleNameAttrib)) ? attrs.get(middleNameAttrib).get().toString()
                    : "";

            String orgName = (null != attrs.get(orgAttrib)) ? attrs.get(orgAttrib).get().toString() : "";
            Organization orgObject = new Organization(orgName, orgName);

            String title = (null != attrs.get(titleAttrib)) ? attrs.get(titleAttrib).get().toString() : "";

            String[] splitCn = attrs.get(fullNameAttrib).get().toString().split(", ");
            String preferredName = (splitCn.length > 1) ? splitCn[1] : firstName;

            person = new Person(accountId, firstName, middleName, lastName, preferredName);

            person.setTitle(title);
            person.setParentOrganization(orgObject);
            person.setEmail(email);
            
            if (additionalProperties != null) 
            {
                HashMap<String, String> propertiesMap = new HashMap<String, String>(); 
                for (String property : additionalProperties) 
                { 
                    //Some additional configurated properties may not be available for all users.  Do not  
                    //halt on those properties, just move on. 
                    try 
                    { 
                        propertiesMap.put(property, attrs.get(property).get().toString()); 
                    } 
                    catch(Exception ex) 
                    { 
                        if(log.isInfoEnabled()) 
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
            log.debug("Error instantiating person object.", e);
        }

        if (null != person)
        {
            people.put(person.getAccountId(), person);
        }

        return null;
    }

    /**
     * @param inAccountAttrib
     *            the accountAttrib to set.
     */
    public void setAccountAttrib(final String inAccountAttrib)
    {
        this.accountAttrib = inAccountAttrib;
    }

    /**
     * @param inFirstNameAttrib
     *            the firstNameAttrib to set.
     */
    public void setFirstNameAttrib(final String inFirstNameAttrib)
    {
        this.firstNameAttrib = inFirstNameAttrib;
    }

    /**
     * @param inLastNameAttrib
     *            the lastNameAttrib to set.
     */
    public void setLastNameAttrib(final String inLastNameAttrib)
    {
        this.lastNameAttrib = inLastNameAttrib;
    }

    /**
     * @param inMiddleNameAttrib
     *            the middleNameAttrib to set.
     */
    public void setMiddleNameAttrib(final String inMiddleNameAttrib)
    {
        this.middleNameAttrib = inMiddleNameAttrib;
    }

    /**
     * @param inOrgAttrib
     *            the orgAttrib to set.
     */
    public void setOrgAttrib(final String inOrgAttrib)
    {
        this.orgAttrib = inOrgAttrib;
    }

    /**
     * @param inTitleAttrib
     *            the titleAttrib to set.
     */
    public void setTitleAttrib(final String inTitleAttrib)
    {
        this.titleAttrib = inTitleAttrib;
    }

    /**
     * @param inFullNameAttrib
     *            the fullNameAttrib to set.
     */
    public void setFullNameAttrib(final String inFullNameAttrib)
    {
        this.fullNameAttrib = inFullNameAttrib;
    }

    /**
     * @param inEmailAttrib
     *            the emailAttrib to set.
     */
    public void setEmailAttrib(final String inEmailAttrib)
    {
        this.emailAttrib = inEmailAttrib;
    }

    /**
     * @param inSupportEmail
     *            the supportEmail to set.
     */
    public void setSupportEmail(final String inSupportEmail)
    {
        this.supportEmail = inSupportEmail;
    }

    /**
     * Set default LdapTemplate to use. Inherited from PeopleAppender
     * but not used in this case.
     * @param inLdapTemplate The LdapTemplate to use.
     */
    public void setDefaultLdapTemplate(final LdapTemplate inLdapTemplate)
    {
        //not needed here, do nothing.        
    }

	/**
	 * @param inAdditionalProperties the additionalProperties to set
	 */
	public void setAdditionalProperties(final List<String> inAdditionalProperties)
	{
		additionalProperties = inAdditionalProperties;
	}
}
