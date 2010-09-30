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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.server.Rotator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RotatingCacheClient.
 * 
 */
public class RotatingCacheClientTest
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
     * Read {@link Rotator}.
     */
    private Rotator<Cache> readRotator = context.mock(Rotator.class, "readRotator");

    /**
     * Write {@link Rotator}.
     */
    private Rotator<Cache> writeRotator = context.mock(Rotator.class, "writeRotator");

    /**
     * {@link Cache}.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * System under test.
     */
    private RotatingCacheClient sut = new RotatingCacheClient(readRotator, writeRotator);

    /**
     * Test.
     */
    @Test
    public void testaddToSet()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).addToSet(null, null);
            }
        });

        sut.addToSet(null, null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testaddToTopOfListMultiValue()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).addToTopOfList(with(any(String.class)), with(any(List.class)));
            }
        });

        sut.addToTopOfList("key", new ArrayList());
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testaddToTopOfList()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).addToTopOfList(null, 5L);
            }
        });

        sut.addToTopOfList(null, 5L);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testclear()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(readRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).clear();
            }
        });

        sut.clear();
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testdelete()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).delete(null);
            }
        });

        sut.delete(null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testdeleteList()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).deleteList(null);
            }
        });

        sut.deleteList(null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testget()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(readRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).get(null);
            }
        });

        sut.get(null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testgetListMaxEntries()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(readRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).getList(null, 5);
            }
        });

        sut.getList(null, 5);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testgetList()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(readRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).getList(null);
            }
        });

        sut.getList(null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testmultiGet()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(readRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).multiGet(null);
            }
        });

        sut.multiGet(null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testmultiGetList()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(readRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).multiGetList(null);
            }
        });

        sut.multiGetList(null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testremoveFromList()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).removeFromList(null, null);
            }
        });

        sut.removeFromList(null, null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testremoveFromLists()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).removeFromLists(null, null);
            }
        });

        sut.removeFromLists(null, null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testremoveFromSet()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).removeFromSet(null, null);
            }
        });

        sut.removeFromSet(null, null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testset()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).set(null, null);
            }
        });

        sut.set(null, null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testsetList()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).setList(null, null);
            }
        });

        sut.setList(null, null);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testsetListCAS()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(writeRotator).getNext();
                will(returnValue(cache));

                oneOf(cache).setListCAS(null, null);
            }
        });

        sut.setListCAS(null, null);
        context.assertIsSatisfied();
    }

}
