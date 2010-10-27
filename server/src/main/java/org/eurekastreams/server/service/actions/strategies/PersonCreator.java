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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.domain.TabType;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Create person resource strategy.
 */
public class PersonCreator implements ResourcePersistenceStrategy<Person>
{
    /**
     * The person mapper.
     */
    private PersonMapper personMapper;

    /**
     * The tab mapper.
     */
    private TabMapper tabMapper;

    /**
     * The Organization mapper.
     */
    private OrganizationMapper organizationMapper;

    /**
     * Mapper to get the readonly streams.
     */
    private DomainMapper<Long, List<Stream>> readonlyStreamsMapper;

    /**
     * List of the names of readonly streams to add to a person, in order.
     */
    private List<String> readOnlyStreamsNameList;
    
    /**
     * List of StartPage Tabs to create when adding a new user.
     */
    private final List<String> startPageTabs;

    /**
     * Constructor.
     *
     * @param inPersonMapper
     *            person mapper.
     * @param inTabMapper
     *            tab mapper.
     * @param inOrganizationMapper
     *            org mapper
     * @param inReadonlyStreamsMapper
     *            mapper to get the readonly streams
     * @param inReadOnlyStreamsNameList
     *            List of the names of readonly streams to add to a person, in order
     * @param inStartPageTabs - list of tabs to be created on the start page.
     */
    public PersonCreator(final PersonMapper inPersonMapper, final TabMapper inTabMapper,
            final OrganizationMapper inOrganizationMapper,
            final DomainMapper<Long, List<Stream>> inReadonlyStreamsMapper, //
            final List<String> inReadOnlyStreamsNameList,
            final List<String> inStartPageTabs)
    {
        personMapper = inPersonMapper;
        tabMapper = inTabMapper;
        organizationMapper = inOrganizationMapper;
        readonlyStreamsMapper = inReadonlyStreamsMapper;
        readOnlyStreamsNameList = inReadOnlyStreamsNameList;
        startPageTabs = inStartPageTabs;
    }

    /**
     * Gets a new person.
     *
     * @param inActionContext
     *            the action context
     * @param inFields
     *            the fields.
     * @return a new person.
     */
    public Person get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        // create a tab groups for the person
        TabGroup profileTabGroup = new TabGroup();
        TabGroup startTabGroup = new TabGroup();

        // create all tabs needed for an person profile

        // add the templates to the tab group
        profileTabGroup.addTab(new Tab(tabMapper.getTabTemplate(TabType.PERSON_ABOUT)));

        // These tabs create their own templates based on other templates.
        // It was decided that we will not have the Application
        // tab on the person profile yet. The
        // TabType.APP template is therefore not being made in the database by
        // the population script.
        // Tab personAppTab = new Tab(new
        // TabTemplate(tabMapper.getTabTemplate(TabType.APP)));
        for(String tabType : startPageTabs)
        {
            startTabGroup.addTab(new Tab(new TabTemplate(tabMapper.getTabTemplate(tabType))));
        }

        // create the person
        Person person = new Person((String) inFields.get("accountId"), (String) inFields.get("firstName"),
                (String) inFields.get("middleName"), (String) inFields.get("lastName"), (String) inFields
                        .get("preferredName"));
        person.setProfileTabGroup(profileTabGroup);
        person.setStartTabGroup(startTabGroup);
        person.setEmail((String) inFields.get("email"));
        person.setOpenSocialId(UUID.randomUUID().toString());

        // Make the default view for a person
        StreamScope personScope = new StreamScope(ScopeType.PERSON, (String) inFields.get("accountId"));

        person.setStreamScope(personScope);

        Set<StreamScope> defaultScopeList = new HashSet<StreamScope>();
        defaultScopeList.add(personScope);

        List<Stream> streams = getStreamsForPerson();

        person.setStreams(streams);

        // Set hidden line indexes.
        person.setStreamViewHiddenLineIndex(streams.size() - 1);
        person.setGroupStreamHiddenLineIndex(3);

        // if organization is not supplied, get root from Org mapper
        if (inFields.containsKey("organization"))
        {
            Organization org = (Organization) inFields.get("organization");
            person.setParentOrganization(org);
        }
        else
        {
            person.setParentOrganization(organizationMapper.getRootOrganization());
        }

        // remove public settable properties already handled from map so updater
        // doesn't do them again.
        inFields.remove("organization");
        inFields.remove("email");
        
        return person;
    }

    /**
     * Persists a new person and make them follow themselves.
     *
     * @param inActionContext
     *            the action context
     * @param inFields
     *            the fields.
     * @param inPerson
     *            the person to persist.
     * @throws Exception
     *             On error.
     */
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final Person inPerson) throws Exception
    {
        personMapper.insert(inPerson);

        // sets the destination entity id for the person's stream scope
        inPerson.getStreamScope().setDestinationEntityId(inPerson.getId());

        // this has to be the last thing we do, since it updates the person behind the back of the object model
        personMapper.addFollower(inPerson.getId(), inPerson.getId());
    }

    /**
     * Get the ordered list of streams for the new person.
     *
     * @return the ordered list of streams for the new person
     */
    private List<Stream> getStreamsForPerson()
    {
        List<Stream> allStreams = readonlyStreamsMapper.execute(0L);
        List<Stream> output = new ArrayList<Stream>();
        for (String listName : readOnlyStreamsNameList)
        {
            for (Stream stream : allStreams)
            {
                if (stream.getName().equalsIgnoreCase(listName))
                {
                    output.add(stream);
                    allStreams.remove(stream);
                    break;
                }
            }
        }
        return output;
    }
}
