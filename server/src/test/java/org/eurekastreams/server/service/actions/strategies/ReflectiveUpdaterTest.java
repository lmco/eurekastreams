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

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.domain.stream.Activity;
import org.junit.Test;

/**
 * Tests the updater.
 */
public class ReflectiveUpdaterTest
{
    /**
     * Test updating a list of data.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testUpdateList() throws Exception
    {
        String message = "lists should be set appropriately";

        TabGroup sut = new TabGroup();

        ArrayList<Tab> tabs = new ArrayList<Tab>();
        tabs.add(new Tab("name", Layout.ONECOLUMN));
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("tabs", tabs);

        ReflectiveUpdater reflector = new ReflectiveUpdater();
        reflector.setProperties(sut, map);

        assertEquals(message, tabs, sut.getTabs());
    }

    /**
     * Test updating a String.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void testUpdateString() throws Exception
    {

        String message = "lists should be set appropriately";

        final String email = "joe@schmoe.com";
        final String newEmail = "joe@gmail.com";
        Person person = new Person("jschmoe", "Joe", "X", "Schmoe", "Joey");
        person.setEmail(email);
        person.setWorkPhone("1234567890");
        person.setTitle("Better than you!");
        person.setLocation("19406");
        person.setJobDescription("ahhh, snuts!");

        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("email", newEmail);

        ReflectiveUpdater reflector = new ReflectiveUpdater();
        reflector.setProperties(person, map);

        assertEquals(message, newEmail, person.getEmail());

    }

    /**
     * Test updating a Set.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void testUpdateSet() throws Exception
    {

        String message = "sets should be set appropriately";

        final DomainGroup group = new DomainGroup();
        final Person person = new Person("jschmoe", "Joe", "X", "Schmoe", "Joey");
        person.setEmail("joe@schmoe.com");
        person.setWorkPhone("1234567890");
        person.setTitle("Better than you!");
        person.setLocation("19406");
        person.setJobDescription("ahhh, snuts!");

        HashSet<Person> coords = new HashSet<Person>();
        coords.add(person);

        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("coordinators", coords);

        ReflectiveUpdater reflector = new ReflectiveUpdater();
        reflector.setProperties(group, map);

        assertEquals(message, coords, group.getCoordinators());

    }

    /**
     * Test updating a Set.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void testUpdateHashMap() throws Exception
    {

        String message = "maps should be set appropriately";

        final Activity activity = new Activity();

        HashMap<String, String> baseObject = new HashMap<String, String>();
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("baseObject", baseObject);

        ReflectiveUpdater reflector = new ReflectiveUpdater();
        reflector.setProperties(activity, map);

        assertEquals(message, baseObject, activity.getBaseObject());

    }

    // TODO: Determine if this is needed anymore, activity no longer has a float.
    // /**
    // * Test updating a float.
    // *
    // * @throws Exception
    // * not expected.
    // */
    // @Test
    // public final void testUpdateFloat() throws Exception
    // {
    //
    // final Activity activity = new Activity();
    //
    // HashSet<Activity> coords = new HashSet<Activity>();
    // coords.add(activity);
    //
    // float priority = 1F;
    // HashMap<String, Serializable> map = new HashMap<String, Serializable>();
    // map.put("priority", priority);
    //
    // ReflectiveUpdater reflector = new ReflectiveUpdater();
    // reflector.setProperties(activity, map);
    //
    // assertEquals(priority, activity.getPriority(), 0);
    //
    // }

    /**
     * Test updating a boolean.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void testUpdateBoolean() throws Exception
    {

        final Gadget gadget = new Gadget(null, 1, 1, null, "");

        boolean minimized = true;
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("minimized", minimized);

        ReflectiveUpdater reflector = new ReflectiveUpdater();
        reflector.setProperties(gadget, map);

        assertEquals(true, gadget.isMinimized());

    }

    /**
     * Test updating a date.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void testUpdateDate() throws Exception
    {

        String message = "sets should be set appropriately";

        final Activity activity = new Activity();

        HashSet<Activity> coords = new HashSet<Activity>();
        coords.add(activity);

        Date date = new Date();
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("updated", date);

        ReflectiveUpdater reflector = new ReflectiveUpdater();
        reflector.setProperties(activity, map);

        assertEquals(message, date, activity.getUpdated());

    }
}
