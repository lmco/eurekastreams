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
package org.eurekastreams.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

/**
 * This class is responsible for testing the JPA Implementation of the Person Mapper interface. The tests contained in
 * here ensure proper interaction with the database.
 */
public class PersonMapperTest extends DomainEntityMapperTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * JpaPersonMapper - system under test.
     */
    @Autowired
    private PersonMapper jpaPersonMapper;

    /**
     * JpaOrganizationMapper.
     */
    @Autowired
    private OrganizationMapper jpaOrganizationMapper;

    /**
     * JpaOrganizationMapper.
     */
    @Autowired
    private TabGroupMapper jpaTabGroupMapper;

    /**
     * Autowired QueryOptimizer.
     */
    @Autowired
    private QueryOptimizer queryOptimizer;

    /**
     * PersonId for Ford Prefect.
     */
    private final int fordPersonId = 42;

    /**
     * PersonId for MrBurns.
     */
    private final int mrburnsPersonId = 99;

    /**
     * Memcached.
     */
    private Cache memcached = context.mock(Cache.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        Person.setEntityCacheUpdater(null);
    }

    /**
     * Teardown method.
     */
    @After
    public void teardown()
    {
        Person.setEntityCacheUpdater(null);
    }

    /**
     * Test we can get the parent org id without loading the org.
     */
    @Test
    public void testParentOrgId()
    {
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        assertEquals(new Long(5L), ford.getParentOrgId());
    }

    /**
     * Test the DBUnit XML Dataset - person.
     */
    @Test
    public void testDBUnitDatasetPerson()
    {
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        assertEquals("Expected the first name of the user with accountId='fordp' to be 'Ford' from DBUnit setup.",
                "Ford", ford.getFirstName());
        assertEquals("Expected the last name of the user with accountId='fordp' to be 'Prefect' from DBUnit setup.",
                "Prefect", ford.getLastName());
        assertEquals("Expected the preferred name of the user with accountId='fordp' "
                + "to be 'Volgon-Swatter' from DBUnit setup.", "Volgon-Swatter", ford.getPreferredName());
    }

    /**
     * Test the DBUnit XML Dataset - tabs.
     */
    @Test
    public void testDBUnitDatasetTabs()
    {
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        TabGroup tabGroup = ford.getStartTabGroup();
        List<Tab> tabs = tabGroup.getTabs();

        // Assert the order is 1,2,3
        assertEquals("Expected Ford's first tab in his first TabGroup to be called 'Ford Tab 1' from DBUnit setup.",
                "Ford Tab 1", tabs.get(0).getTabName());
        assertEquals("Expected Ford's first tab in his first TabGroup to be called 'Ford Tab 2' from DBUnit setup.",
                "Ford Tab 2", tabs.get(1).getTabName());
        assertEquals("Expected Ford's first tab in his first TabGroup to be called 'Ford Tab 3' from DBUnit setup.",
                "Ford Tab 3", tabs.get(2).getTabName());
    }

    /**
     * Test the domain entity name of the mapper - used for parent class generic operations.
     */
    @Test
    public void testGetDomainEntityName()
    {
        assertEquals("Domain entity name should be 'Person'", "Person", jpaPersonMapper.getDomainEntityName());
    }

    /**
     * Test inserting a person.
     */
    @Test
    public void testInsert()
    {
        final long id = 4231L;

        Organization org = jpaOrganizationMapper.findById(6L);
        TabGroup tg = jpaTabGroupMapper.findById(id);

        Person p = new Person("ndtyson", "Neil", "d", "deGrasse Tyson", "Dr. To You");
        p.setWorkPhone("1234567890");
        p.setTitle("Better than you!");
        p.setEmail("foo.blah@example.com");
        p.setLocation("19406");
        p.setJobDescription("some description!");
        p.setParentOrganization(org);
        p.setStartTabGroup(tg);
        p.setProfileTabGroup(tg);
        jpaPersonMapper.insert(p);
        assertTrue("Inserting a Person did not get a positive id.",
                jpaPersonMapper.findByAccountId("ndtyson").getId() > 0);
    }

    /**
     * Test the finding of orphaned people. Any orphaned person is defined as a person who has a parent organization
     * assignment but does not have that parent organization listed in their related organization list.
     */
    @Test
    public void testFindOrphanedPeople()
    {
        List<Person> orphanedPeople = jpaPersonMapper.findOrphanedPeople();
        assertTrue("The list of orphaned people was not correct; expected 1, got " + orphanedPeople.size(),
                orphanedPeople.size() == 1);

        Person mrburns = jpaPersonMapper.findById(mrburnsPersonId);
        assertTrue("The list did not contain the correct person", orphanedPeople.contains(mrburns));
    }

    /**
     * Test the purging of related organizations for all people.
     */
    @Test
    public void testPurgeRelatedOrganizations()
    {
        Person ford = jpaPersonMapper.findById(fordPersonId);
        assertTrue("Getting related organizations list was empty", ford.getRelatedOrganizations().size() == 2);

        // method invocation under test
        jpaPersonMapper.purgeRelatedOrganizations();

        getEntityManager().clear();

        ford = jpaPersonMapper.findById(fordPersonId);
        assertTrue("Getting related organizations list was not empty", ford.getRelatedOrganizations().size() == 0);
    }

    /**
     * Test inserting a person w/profile properties.
     */
    @Test
    public void testInsertWithProfileProperties()
    {
        final long id = 4231L;

        Organization org = jpaOrganizationMapper.findById(6L);
        TabGroup tg = jpaTabGroupMapper.findById(id);

        Person p = new Person("yoyojoe", "Joe", "hey", "Yoyo", "Call Me Joe");
        p.setWorkPhone("1234567890");
        p.setTitle("Better than you!");
        p.setEmail("foo.blah@example.com");
        p.setLocation("19406");
        p.setJobDescription("some description!");
        p.setAvatarId("avatar");
        p.setParentOrganization(org);
        p.setStartTabGroup(tg);
        p.setProfileTabGroup(tg);

        // phone types not set should return null.
        assertNull("Phone types not set should return null", p.getCellPhone());
        assertNull("Phone types not set should return null", p.getFax());

        p.setCellPhone("5555555555");
        p.setFax("4444444444");

        jpaPersonMapper.insert(p);
        assertTrue("Inserting a Person did not get a positive id.",
                jpaPersonMapper.findByAccountId("yoyojoe").getId() > 0);

        this.getEntityManager().clear();
        Person sut = jpaPersonMapper.findByAccountId("yoyojoe");
        assertTrue(sut.getTitle().equals("Better than you!"));
        assertTrue(sut.getEmail().equals("foo.blah@example.com"));
        assertTrue(sut.getLocation().equals("19406"));
        assertTrue(sut.getJobDescription().equals("some description!"));
        assertTrue("1234567890".equals(sut.getWorkPhone()));
        assertTrue("5555555555".equals(sut.getCellPhone()));
        assertTrue("4444444444".equals(sut.getFax()));
        assertTrue("avatar".equals(sut.getAvatarId()));
        assertEquals(id, sut.getProfileTabGroup().getId());
        assertEquals(id, sut.getStartTabGroup().getId());
    }

    /**
     * Test adding a tab to a user's tab group persists when we update the person.
     */
    @Test
    public void testUpdateAddNewTab()
    {
        Person ford = jpaPersonMapper.findById(fordPersonId);
        ford.getStartTabGroup().getTabs().add(new Tab("Foo", Layout.THREECOLUMN));
        jpaPersonMapper.flush();

        getEntityManager().clear();

        ford = jpaPersonMapper.findById(fordPersonId);
        assertEquals("Attemped updating a Person after adding a Tab to his first "
                + "TabGroup, then clearing the EntityManager.  "
                + "Expected to see the new tab after re-loading the Person.", "Foo", ford.getStartTabGroup().getTabs()
                .get(ford.getStartTabGroup().getTabs().size() - 1).getTabName());
    }

    /**
     * Test inserting a person that already exists in the database throws a PersistenceException exception, with an
     * account id of different case - casing shouldn't matter - the account ids go into the database in lower-case.
     */
    @Test
    @ExpectedException(PersistenceException.class)
    public void testInsertThrowsExceptionWithExistingAccountIdOfSameCase()
    {
        jpaPersonMapper.insert(new Person("fordp", "Niels", "A", "Bohr", "Atom-Head"));
    }

    /**
     * Test inserting a person that already exists in the database throws a PersistenceException exception, with an
     * account id of different case - casing shouldn't matter - the account ids go into the database in lower-case.
     */
    @Test
    @ExpectedException(PersistenceException.class)
    public void testInsertThrowsExceptionWithExistingAccountIdOfDifferentCase()
    {
        jpaPersonMapper.insert(new Person("fOrDp", "Niels", "A", "Bohr", "Atom-Head"));
    }

    /**
     * Make sure we can find a Person when we ask for the account id with the correct case.
     */
    @Test
    public void testFindByAccountIdCorrectCase()
    {
        assertEquals(fordPersonId, jpaPersonMapper.findByAccountId("fordp").getId());
    }

    /**
     * Test to see if ford prefect is found when searching by the lastname "pre" pre-letters.
     */
    @Test
    public void testFindByLastNamePrefixForMrPrefect()
    {
        assertEquals(fordPersonId, jpaPersonMapper.findPeopleByPrefix("Pre").get(0).getId());
    }

    /**
     * Test to see if no results are returned for search with first name.
     */
    @Test
    public void testFindByLastNamePrefixForManyPeople()
    {
        final int id = 142;
        assertEquals(2, jpaPersonMapper.findPeopleByPrefix("P").size());
        assertEquals(fordPersonId, jpaPersonMapper.findPeopleByPrefix("P").get(0).getId());
        assertEquals(id, jpaPersonMapper.findPeopleByPrefix("P").get(1).getId());
    }

    /**
     * Test to see if Ford Prefect and Ford Prefect are found for the pre-letter p.
     */
    @Test
    public void testFindByLastNamePrefixWithFirstName()
    {
        assertEquals(2, jpaPersonMapper.findPeopleByPrefix("Prefect, Volgon").size());
    }

    /**
     * Test to see if no results are returned for search with type.
     */
    @Test
    public void testFindByLastNamePrefixWithFirstNameNoSpace()
    {
        List<Person> people = jpaPersonMapper.findPeopleByPrefix("Perfect,Ford");
        assertTrue("Returned list should be empty", people.size() == 0);
    }

    /**
     * Test to see null is returned if no one is in the list.
     */
    @Test
    public void testFindByLastNamePrefixReturnNull()
    {
        List<Person> people = jpaPersonMapper.findPeopleByPrefix("Poop");
        assertTrue("Returned list should be empty", people.size() == 0);

    }

    /**
     * Make sure we can't find a non-existent person.
     */
    @Test
    public void testFindByNonExistentAccountId()
    {
        assertEquals(null, jpaPersonMapper.findByAccountId("nonexistentuser"));
    }

    /**
     * Make sure we can find a Person when we ask for the account id with the incorrect case.
     */
    @Test
    public void testFindByAccountIdMixedCase()
    {
        assertEquals(fordPersonId, jpaPersonMapper.findByAccountId("foRDp").getId());
    }

    /**
     * Make sure we can find people by opensocial ids.
     */
    @Test
    public void testFindPeopleByOpenSocialIds()
    {
        ArrayList<String> openSocialIds = new ArrayList<String>();
        openSocialIds.add("2d359911-0977-418a-9490-57e8252b1a42");
        openSocialIds.add("2d359911-0977-418a-9490-57e8252b1142");
        assertEquals("2 Person objects should have been returned", 2, jpaPersonMapper.findPeopleByOpenSocialIds(
                openSocialIds).size());
    }

    /**
     * Make sure we can find only the Person objects from valid opensocial id inputs.
     */
    @Test
    public void testFindPeopleByOpenSocialIdsWithInvalidId()
    {
        ArrayList<String> openSocialIds = new ArrayList<String>();
        openSocialIds.add("2d359911-0977-418a-9490-57e8252b1a42");
        openSocialIds.add("invalidid");
        assertEquals("1 Person object should have been returned", 1, jpaPersonMapper.findPeopleByOpenSocialIds(
                openSocialIds).size());
    }

    /**
     * Make sure we can find followed people by follower opensocial ids.
     */
    @Test
    public void testfindPeopleFollowedUsingFollowerOpenSocialIds()
    {
        ArrayList<String> openSocialIds = new ArrayList<String>();
        openSocialIds.add("2d359911-0977-418a-9490-57e8252b1a42");
        openSocialIds.add("2d359911-0977-418a-9490-57e8252b1a98");
        assertEquals("3 Person objects should have been returned", 3, jpaPersonMapper
                .findPeopleFollowedUsingFollowerOpenSocialIds(openSocialIds).size());
    }

    /**
     * Test the tab order on persisting the Person. The ORM should maintain the tab order when reordered and persisted.
     */
    // TODO: Consider moving this test to the TabGroupMapper.
    @Test
    public void testTabOrder()
    {
        final int expectedTabCount = 3;

        // Load Ford, and rearrange his tabs
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        TabGroup tabGroup = ford.getStartTabGroup();
        List<Tab> tabs = tabGroup.getTabs();

        Tab t2 = tabs.get(1);
        tabs.remove(t2);
        tabs.add(t2);

        // update all modified entities
        jpaPersonMapper.flush();

        // clear the entity manager
        getEntityManager().clear();

        // reload
        ford = jpaPersonMapper.findByAccountId("fordp");
        tabGroup = ford.getStartTabGroup();
        tabs = tabGroup.getTabs();

        // assert there's 3 tabs
        assertEquals("Ford Prefect should have 3 (non-deleted) tabs in the DBUnit fixture", expectedTabCount, tabs
                .size());

        // assert the order is 1,3,2
        assertEquals("Ford Tab 1", tabs.get(0).getTabName());
        assertEquals("Ford Tab 3", tabs.get(1).getTabName());
        assertEquals("Ford Tab 2", tabs.get(2).getTabName());
    }

    /**
     * Test inserting a user, then finding the user by ID when the object is still in object cache.
     */
    @Test
    public void testFindByIdWhenCached()
    {
        assertSame("When finding a Person by ID and that object exists in object cache, "
                + "expected the cached instance to be returned.", jpaPersonMapper.findById(fordPersonId),
                jpaPersonMapper.findById(fordPersonId));
    }

    /**
     * Test add follower.
     */
    @Test
    public void testAddFollower()
    {
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        Person sagan = jpaPersonMapper.findByAccountId("csagan");

        assertEquals("followers/followings should be 0 initially", 0, ford.getFollowersCount());
        assertEquals("followers/followings should be 0 initially", 0, ford.getFollowingCount());
        assertEquals("followers/followings should be 0 initially", 0, sagan.getFollowersCount());
        assertEquals("followers/followings should be 0 initially", 0, sagan.getFollowingCount());

        jpaPersonMapper.addFollower(ford.getId(), sagan.getId());
        getEntityManager().clear();

        ford = jpaPersonMapper.findByAccountId("fordp");
        sagan = jpaPersonMapper.findByAccountId("csagan");

        assertEquals("sagan should have 1 follower", 1, sagan.getFollowersCount());
        assertEquals("sagan follows no one", 0, sagan.getFollowingCount());

        assertEquals("ford should have no followers", 0, ford.getFollowersCount());
        assertEquals("ford should be following sagan", 1, ford.getFollowingCount());

        // test case for add when relationship already present.
        jpaPersonMapper.addFollower(ford.getId(), sagan.getId());
        getEntityManager().clear();

        ford = jpaPersonMapper.findByAccountId("fordp");
        sagan = jpaPersonMapper.findByAccountId("csagan");

        // verify nothing has changed counts should be same.
        assertEquals("Followers count changed after duplicate add.", 1, sagan.getFollowersCount());
        assertEquals("Following count changed after duplicate add", 0, sagan.getFollowingCount());

        assertEquals("follower's Followers count changed after duplicate add.", 0, ford.getFollowersCount());
        assertEquals("follower's Following count changed after duplicate add", 1, ford.getFollowingCount());
    }

    /**
     * Test remove follower.
     */
    @Test
    public void testRemoveFollower()
    {
        Person smithers = jpaPersonMapper.findByAccountId("smithers");
        Person burns = jpaPersonMapper.findByAccountId("mrburns");

        // assert initial state correct from DB unit.
        assertEquals("burns should have 1 follower", 1, burns.getFollowersCount());
        assertEquals("burns should not be following any", 0, burns.getFollowingCount());

        assertEquals("smithers should have no followers", 0, smithers.getFollowersCount());
        assertEquals("smithers should be following burns", 1, smithers.getFollowingCount());

        jpaPersonMapper.removeFollower(smithers.getId(), burns.getId());
        getEntityManager().clear();

        smithers = jpaPersonMapper.findByAccountId("smithers");
        burns = jpaPersonMapper.findByAccountId("mrburns");

        assertEquals("followers/followings should be 0 after removal", 0, smithers.getFollowersCount());
        assertEquals("followers/followings should be 0 after removal", 0, smithers.getFollowingCount());
        assertEquals("followers/followings should be 0 after removal", 0, burns.getFollowersCount());
        assertEquals("burns followings should be 0 after removal", 0, burns.getFollowingCount());

        // cover case where remove is called on relationship that doesn't exist
        // (no-op).
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        Person sagan = jpaPersonMapper.findByAccountId("csagan");

        // nothing really to assert after this, just verify everything is the
        // same.
        jpaPersonMapper.removeFollower(ford.getId(), sagan.getId());
        getEntityManager().clear();

        ford = jpaPersonMapper.findByAccountId("fordp");
        sagan = jpaPersonMapper.findByAccountId("csagan");

        assertEquals("followers/followings should be 0 after removal", 0, smithers.getFollowersCount());
        assertEquals("followers/followings should be 0 after removal", 0, smithers.getFollowingCount());
        assertEquals("followers/followings should be 0 after removal", 0, burns.getFollowersCount());
        assertEquals("burns followings should be 0 after removal", 0, burns.getFollowingCount());

    }

    /**
     * Test get followers.
     */
    @Test
    public void testGetFollowers()
    {
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        Person sagan = jpaPersonMapper.findByAccountId("csagan");

        assertEquals("followers/followings should be 0 initially", 0, ford.getFollowersCount());
        assertEquals("followers/followings should be 0 initially", 0, ford.getFollowingCount());
        assertEquals("followers/followings should be 0 initially", 0, sagan.getFollowersCount());
        assertEquals("followers/followings should be 0 initially", 0, sagan.getFollowingCount());

        jpaPersonMapper.addFollower(ford.getId(), sagan.getId());
        getEntityManager().clear();

        ford = jpaPersonMapper.findByAccountId("fordp");
        sagan = jpaPersonMapper.findByAccountId("csagan");

        assertEquals("sagan should have 1 follower", 1, sagan.getFollowersCount());
        assertEquals("sagan follows no one", 0, sagan.getFollowingCount());
        PagedSet<Person> connections = jpaPersonMapper.getFollowers(sagan.getAccountId(), 0, 6);
        assertNotNull(connections);
        assertEquals("sagan is followed by ford", "fordp", connections.getPagedSet().get(0).getAccountId());
    }

    /**
     * Test for isFollowing Method.
     */
    @Test
    public void testIsFollowing()
    {
        assertTrue(jpaPersonMapper.isFollowing("smithers", "mrburns"));
        assertTrue(jpaPersonMapper.isFollowing("mrburns", "smithers"));
        assertFalse(jpaPersonMapper.isFollowing("abc", "123"));
    }

    /**
     * Test findByOpenSocialId.
     */
    @Test
    public void testFindByOpenSocialId()
    {
        // from dataset.xml
        String osid = "2d359911-0977-418a-9490-57e8252b1a42";
        String bogusOsid = "bogusOsid";

        assertNotNull(jpaPersonMapper.findByOpenSocialId(osid));
        assertNull(jpaPersonMapper.findByOpenSocialId(bogusOsid));
    }

    /**
     * Test that calling getDescendantOrgStrategy() when not set throws NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    public void testGetDescendantOrgStrategyWhenNotSet()
    {
        PersonMapper personMapper = new PersonMapper(queryOptimizer, null);
        personMapper.getDescendantOrgStrategy();
    }

    /**
     * Test the profile properties.
     */
    @Test
    public void testProfileProperties()
    {
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        assertEquals(1, ford.getJobs().size());
        assertEquals("Aerospace", ford.getJobs().get(0).getIndustry());

        assertEquals(1, ford.getSchoolEnrollments().size());
        assertEquals("BS Computer Science", ford.getSchoolEnrollments().get(0).getDegree());

        assertEquals(1, ford.getBackground().getBackgroundItems(BackgroundItemType.INTEREST).size());
        assertEquals("Earthlings", ford.getBackground().getBackgroundItems(BackgroundItemType.INTEREST).get(0)
                .getName());
    }

    /**
     * Test the updater of the Person entity.
     */
    @Test
    public void testUpdatedCallback()
    {
        // wire up the updater
        DummyUpdater dummyUpdater = new DummyUpdater();
        Person.setEntityCacheUpdater(dummyUpdater);

        // find a person to update
        Person ford = jpaPersonMapper.findByAccountId("fordp");
        ford.setAvatarId("FOOBAR");

        // make sure the event hasn't fired yet
        assertNull(dummyUpdater.getUpdatedPerson());

        getEntityManager().flush();

        // make sure we received the event
        assertSame(ford, dummyUpdater.getUpdatedPerson());
    }

    /**
     * Test the updater of the Person entity.
     */
    @Test
    public void testPersistedCallback()
    {
        // wire up the updater
        DummyUpdater dummyUpdater = new DummyUpdater();
        Person.setEntityCacheUpdater(dummyUpdater);

        final long id = 4231L;

        Organization org = jpaOrganizationMapper.findById(6L);
        TabGroup tg = jpaTabGroupMapper.findById(id);

        Person p = new Person("ndtyson", "Neil", "d", "deGrasse Tyson", "Dr. To You");
        p.setWorkPhone("1234567890");
        p.setTitle("Better than you!");
        p.setEmail("foo.blah@example.com");
        p.setLocation("19406");
        p.setJobDescription("some description!");
        p.setParentOrganization(org);
        p.setStartTabGroup(tg);
        p.setProfileTabGroup(tg);

        // make sure nothing's happened yet
        assertNull(dummyUpdater.getPersistedPerson());
        jpaPersonMapper.insert(p);

        // make sure we received the event
        assertSame(p, dummyUpdater.getPersistedPerson());
    }

    /**
     * Dummy updater used to test the static Person updater call-back.
     */
    public class DummyUpdater implements EntityCacheUpdater<Person>
    {
        /**
         * The person updated.
         */
        private Person updatedPerson;

        /**
         * The person updated.
         */
        private Person persistedPerson;

        /**
         * Get the updated person.
         *
         * @return the updated person
         */
        public Person getUpdatedPerson()
        {
            return updatedPerson;
        }

        /**
         * Get the person person.
         *
         * @return the persisted person
         */
        public Person getPersistedPerson()
        {
            return persistedPerson;
        }

        /**
         * Updater callback.
         *
         * @param inUpdatedPerson
         *            the person just updated
         */
        @Override
        public void onPostUpdate(final Person inUpdatedPerson)
        {
            updatedPerson = inUpdatedPerson;
        }

        /**
         * Persisted callback.
         *
         * @param inPersistedPerson
         *            the person being persisted
         */
        @Override
        public void onPostPersist(final Person inPersistedPerson)
        {
            persistedPerson = inPersistedPerson;
        }
    }

}
