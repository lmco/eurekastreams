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
package org.eurekastreams.server.domain.stream;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.eurekastreams.server.domain.Person;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for comment entity.
 *
 */
public class CommentTest
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
     * Comment body.
     */
    private String body = "body";

    /**
     * Comment sender.
     */
    private Person author = context.mock(Person.class, "author");

    /**
     * Target activity.
     */
    private Activity targetActivity = context.mock(Activity.class, "target activity");

    /**
     * Comment time sent.
     */
    private Date timeSent = new Date();

    /**
     * System under test.
     */
    private Comment sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new Comment(author, targetActivity, body);
        sut.setTimeSent(timeSent);
    }

    /**
     * Test constructor and get/set used in constructor.
     */
    @Test
    public void testConstructorGetSet()
    {
        assertEquals(author, sut.getAuthor());
        assertEquals(targetActivity, sut.getTarget());
        assertEquals(body, sut.getBody());
        assertEquals(timeSent, sut.getTimeSent());
    }

}
