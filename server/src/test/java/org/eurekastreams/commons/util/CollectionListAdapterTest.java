/*
 * Copyright (c) 2012 Lockheed Martin Corporation
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
package org.eurekastreams.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test CollectionListAdapter.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CollectionListAdapterTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: wrapped collection. */
    private final Collection wrapped = context.mock(Collection.class, "wrapped");

    /** Fixture: parameter collection. */
    private final Collection collection = context.mock(Collection.class, "collection");

    /** Fixture: parameter element. */
    private final Object element = context.mock(Object.class);

    /** Fixture: iterator. */
    private final Iterator iterator = context.mock(Iterator.class);


    /** SUT. */
    private CollectionListAdapter sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new CollectionListAdapter(wrapped);
    }

    /**
     * Test.
     */
    @Test
    public void testAdd1()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).add(element);
                will(returnValue(true));
            }
        });
        assertTrue(sut.add(element));
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAdd2()
    {
        sut.add(0, element);
    }

    /**
     * Test.
     */
    @Test
    public void testAddAll1()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).addAll(collection);
                will(returnValue(true));
            }
        });
        assertTrue(sut.addAll(collection));
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddAll2()
    {
        sut.addAll(0, collection);
    }

    /**
     * Test.
     */
    @Test
    public void testClear()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).clear();
            }
        });
        sut.clear();
    }

    /**
     * Test.
     */
    @Test
    public void testContains()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).contains(element);
                will(returnValue(true));
            }
        });
        assertTrue(sut.contains(element));
    }

    /**
     * Test.
     */
    @Test
    public void testContainsAll()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).containsAll(collection);
                will(returnValue(true));
            }
        });
        assertTrue(sut.containsAll(collection));
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGet()
    {
        sut.get(0);
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIndexOf()
    {
        sut.indexOf(element);
    }

    /**
     * Test.
     */
    @Test
    public void testIsEmpty()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).isEmpty();
                will(returnValue(true));
            }
        });
        assertTrue(sut.isEmpty());
    }

    /**
     * Test.
     */
    @Test
    public void testIterator()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).iterator();
                will(returnValue(iterator));
            }
        });
        assertSame(iterator, sut.iterator());
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLastIndexOf()
    {
        sut.lastIndexOf(element);
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testListIterator1()
    {
        sut.listIterator();
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testListIterator2()
    {
        sut.listIterator(0);
    }

    /**
     * Test.
     */
    @Test
    public void testRemove1()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).remove(element);
                will(returnValue(true));
            }
        });
        assertTrue(sut.remove(element));
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemove2()
    {
        sut.remove(0);
    }

    /**
     * Test.
     */
    @Test
    public void testRemoveAll()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).removeAll(collection);
                will(returnValue(true));
            }
        });
        assertTrue(sut.removeAll(collection));
    }

    /**
     * Test.
     */
    @Test
    public void testRetainAll()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).retainAll(collection);
                will(returnValue(true));
            }
        });
        assertTrue(sut.retainAll(collection));
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSet()
    {
        sut.set(0, element);
    }

    /**
     * Test.
     */
    @Test
    public void testSize()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).size();
                will(returnValue(5));
            }
        });
        assertEquals(5, sut.size());
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSubList()
    {
        sut.subList(0, 0);
    }

    /**
     * Test.
     */
    @Test
    public void testToArray1()
    {
        final Object[] array = new Object[1];
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).toArray();
                will(returnValue(array));
            }
        });
        assertSame(array, sut.toArray());
    }

    /**
     * Test.
     */
    @Test
    public void testToArray2()
    {
        final Object[] array1 = new Object[1];
        final Object[] array2 = new Object[1];
        context.checking(new Expectations()
        {
            {
                oneOf(wrapped).toArray(array1);
                will(returnValue(array2));
            }
        });
        assertSame(array2, sut.toArray(array1));
    }
}
