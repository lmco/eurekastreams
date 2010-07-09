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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Tests deleting a composite stream from the cache.
 */
@TransactionConfiguration(defaultRollback = false)
public class AddCachedCompositeStreamSearchTest extends CachedMapperTest
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
    @Autowired
    AddCachedCompositeStreamSearch sut;
    
    /**
     * Cache.
     */
    @Autowired
    Cache cache;

    /**
     * Stream Search Mock.
     */
    private StreamSearch streamSearchMock = context.mock(StreamSearch.class);
    
    /**
     * User id used in tests.
     */
    private static final long PERSON_ID = 999L;
    
    /**
     * CompositeStream id used in tests.
     */
    private static final long COMPOSITE_STREAM_SEARCH_ID = 1L;
    
    /**
     * CompositeStream id #2 used in tests.
     */
    private static final long COMPOSITE_STREAM_SEARCH_ID_2 = 2L;
    
    /**
     * 
     */
    private static final String CACHE_KEY = CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID;

    /**
     * Setup method.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        cache.delete(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID);
        // check for the composite stream search object in the cache.
        StreamSearch resultCompositeStreamSearchList = 
            (StreamSearch) cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID);
        assertNull(resultCompositeStreamSearchList);
        
        cache.delete(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID);
        // assert the list containing 0 composite stream search id is in the cache.
        List<Long> result = 
            cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID);
        assertNull(result);
    }
    
    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                allowing(streamSearchMock).getId();
                will(returnValue(COMPOSITE_STREAM_SEARCH_ID));                
            }
        });
                
        sut.execute(PERSON_ID, streamSearchMock);

        //assert that cache is empty for compositeStream .
        assertEquals(1, (cache.getList(CACHE_KEY)).size());
        
        context.assertIsSatisfied(); 
    }
        
    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteNonEmptyCompositeStreamSearchList()
    {
        // assert the user has no composite streams in the cache.
        List<Long> result = 
            cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID);
        assertNull(result);
        
        // add one composite stream to the list and put into the cache.
        result = new ArrayList<Long>();
        result.add(COMPOSITE_STREAM_SEARCH_ID_2);
        cache.setList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID, result);
        assertEquals(1, 
                (cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID)).size());

        context.checking(new Expectations()
        {
            {
                allowing(streamSearchMock).getId();
                will(returnValue(COMPOSITE_STREAM_SEARCH_ID));                
            }
        });
        
        sut.execute(PERSON_ID, streamSearchMock);

        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID));
        assertEquals(2, 
                (cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID)).size());
        context.assertIsSatisfied(); 
    }
    
    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithItemAlreadyInList()
    {
        // assert the user has no composite streams in the cache.
        List<Long> result = 
           cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID);
        assertNull(result);
        
        // add one composite stream to the list and put into the cache.
        result = new ArrayList<Long>();
        result.add(COMPOSITE_STREAM_SEARCH_ID_2);
        result.add(COMPOSITE_STREAM_SEARCH_ID);
        cache.setList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID, result);
        assertEquals(2, 
                (cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID)).size());

        context.checking(new Expectations()
        {
            {
                allowing(streamSearchMock).getId();
                will(returnValue(COMPOSITE_STREAM_SEARCH_ID));                
            }
        });
        
        sut.execute(PERSON_ID, streamSearchMock);

        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID));
        assertEquals(2, 
                (cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID)).size());
        context.assertIsSatisfied(); 
    }
    
    /**
     * Test with empty composite stream search list for the person.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteEmptyCompositeStreamSearchList()
    {
        context.checking(new Expectations()
        {
            {
                allowing(streamSearchMock).getId();
                will(returnValue(COMPOSITE_STREAM_SEARCH_ID));                
            }
        });
        
        sut.execute(PERSON_ID, streamSearchMock);

        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID));
        assertEquals(1, 
                (cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID)).size());
        context.assertIsSatisfied(); 
    }  
    
}
