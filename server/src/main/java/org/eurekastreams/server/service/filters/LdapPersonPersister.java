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
package org.eurekastreams.server.service.filters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.ldap.LdapToPersonMapper;
import org.springframework.ldap.core.AttributesMapper;

/**
 * Persists an LDAP person record to the database as a domain object.
 */
public class LdapPersonPersister implements AttributesMapper
{
    /** logger instance. */
    private static Log log = LogFactory.getLog(LdapPersonPersister.class);

    /**
     * The username.
     */
    private String userName;

    /**
     * LDAP to person mapper.
     */
    private LdapToPersonMapper ldapToPerson = null;

    /**
     * Persist resource action.
     */
    private PersistResourceExecution<Person> persistResourceExecution;

    /**
     * Person mapper.
     */
    private PersonMapper personMapper = null;

    /**
     * People list.
     */
    private HashMap<String, Person> people;

    /**
     * Constructor.
     *
     * @param inUserName
     *            the username.
     * @param inPersonMapper
     *            the person mapper.
     * @param inLdapToPerson
     *            the ldap to person mapper.
     * @param inPersistResourceAction
     *            the persist action.
     */
    public LdapPersonPersister(final String inUserName, final PersonMapper inPersonMapper,
            final LdapToPersonMapper inLdapToPerson, final PersistResourceExecution<Person> inPersistResourceAction)
    {
        userName = inUserName;
        ldapToPerson = inLdapToPerson;
        persistResourceExecution = inPersistResourceAction;
        personMapper = inPersonMapper;

        people = new HashMap<String, Person>();
        ldapToPerson.setPeople(people);
    }

    /**
     * Transforms a set of LDAP attributes into a person domain object then persists it.
     *
     * @param attrs
     *            the attributes.
     * @return not used.
     * @throws NamingException
     *             thrown on LDAP exception.
     */
    public Object mapFromAttributes(final Attributes attrs) throws NamingException
    {
        ldapToPerson.mapFromAttributes(attrs);

        if (people.values().size() > 0 && null == personMapper.findByAccountId(userName))
        {
            Person person = (Person) people.values().toArray()[0];

            final HashMap<String, Serializable> personData = person.getProperties(Boolean.FALSE);

            log.trace("Attempting to persist person");

            persistResourceExecution.execute(new TaskHandlerActionContext<PrincipalActionContext>(
                    new PrincipalActionContext()
                    {
                        private static final long serialVersionUID = 8022265753444084805L;

                        @Override
                        public Principal getPrincipal()
                        {
                            throw new RuntimeException("No principal available.");
                        }

                        @Override
                        public Serializable getParams()
                        {
                            return personData;
                        }

                        @Override
                        public Map<String, Object> getState()
                        {
                            return null;
                        }
                    }, null));
        }

        return null;
    }
}
