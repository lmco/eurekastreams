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
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for JobsListStringBridge.
 */
public class JobsListStringBridgeTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private JobsListStringBridge sut = new JobsListStringBridge();

    /**
     * Test objectToString on null object.
     */
    @Test
    public void testObjectToStringWhenNull()
    {
        assertNull(sut.objectToString(null));
    }

    /**
     * Test objectToString when we pass in the wrong type.
     */
    @Test
    public void testObjectToStringOnWrongType()
    {
        assertNull(sut.objectToString(3));
    }

    /**
     * Test objectToString with loaded data.
     */
    @Test
    public void testObjectToString()
    {
        Person person = context.mock(Person.class);
        Job job1 =
                new Job(person, "Volgon International", "Ship Building", "Button-pusher", new Date(), new Date(),
                        "Pushed lots of buttons");
        Job job2 =
                new Job(person, "Foo Foo Bar Bar", "Blah blah blah", "Mooooo!", new Date(), new Date(),
                        "Clever text goes here for clever tests.");

        List<Job> jobs = new ArrayList<Job>();
        jobs.add(job1);
        jobs.add(job2);

        String objToString = sut.objectToString(jobs);

        assertTrue(objToString.contains(" Volgon International "));
        assertTrue(objToString.contains(" Ship Building "));
        assertTrue(objToString.contains(" Button-pusher "));
        assertTrue(objToString.contains(" Pushed lots of buttons "));
        assertTrue(objToString.contains(" Foo Foo Bar Bar "));
        assertTrue(objToString.contains(" Blah blah blah "));
        assertTrue(objToString.contains(" Mooooo! "));
        assertTrue(objToString.contains(" Clever text goes here for clever tests. "));
    }
}
