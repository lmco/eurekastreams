/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.action.request.stream.SetSharedResourceLikeRequest;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.SetSharedResourceLikeMapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SetSharedResourceLikeExecution.
 */
public class SetSharedResourceLikeExecutionTest
{
    /**
     * System under test.
     */
    private SetSharedResourceLikeExecution sut;

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
     * Mocked mapper.
     */
    private final SetLikedResourceStatusMapperFake setLikedResourceStatusMapper = //
    new SetLikedResourceStatusMapperFake();

    /**
     * Mapper to get or insert shared resources.
     */
    private final SharedResourceRequestMapperFake findOrInsertSharedResourceMapper = // \n
    new SharedResourceRequestMapperFake();

    /**
     * Cache.
     */
    private final Cache cache = context.mock(Cache.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetSharedResourceLikeExecution(setLikedResourceStatusMapper, findOrInsertSharedResourceMapper, cache);
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final long personId = 28328L;
        final TaskHandlerActionContext<PrincipalActionContext> taskContext = context
                .mock(TaskHandlerActionContext.class);
        final PrincipalActionContext principalActionContext = context.mock(PrincipalActionContext.class);
        final Principal principal = context.mock(Principal.class);
        final SetSharedResourceLikeRequest actionRequest = new SetSharedResourceLikeRequest("http://foo.com", true);
        final SharedResource sharedResourceFoundOrInserted = new SharedResource("http://fOO.com");
        findOrInsertSharedResourceMapper.setReturnedResource(sharedResourceFoundOrInserted);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        final String expectedCacheKey = CacheKeys.SHARED_RESOURCE_BY_UNIQUE_KEY + "http://foo.com";
        context.checking(new Expectations()
        {
            {
                allowing(taskContext).getActionContext();
                will(returnValue(principalActionContext));

                allowing(principalActionContext).getParams();
                will(returnValue(actionRequest));

                allowing(principalActionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(personId));

                // make sure the cache key is deleted now
                oneOf(cache).delete(expectedCacheKey);

                allowing(taskContext).getUserActionRequests();
                will(returnValue(requests));
            }
        });

        setLikedResourceStatusMapper.setRequest(null);
        sut.execute(taskContext);

        assertEquals("http://foo.com", findOrInsertSharedResourceMapper.getRequest().getUniqueKey());
        assertEquals(personId, setLikedResourceStatusMapper.getRequest().getPersonId());
        assertTrue(setLikedResourceStatusMapper.getRequest().getLikedStatus());
        assertSame(sharedResourceFoundOrInserted, setLikedResourceStatusMapper.getRequest().getSharedResource());

        // make sure the cache key is queued up for task handler to cleanup
        assertEquals(1, requests.size());
        assertEquals("deleteCacheKeysAction", requests.get(0).getActionKey());
        Set<String> params = (Set<String>) requests.get(0).getParams();
        assertEquals(1, params.size());
        for (String p : params)
        {
            assertEquals(expectedCacheKey, p);
        }

        context.assertIsSatisfied();
    }

    /**
     * Fake mapper to store the request so the tests can verify it was made properly.
     */
    private class SetLikedResourceStatusMapperFake implements DomainMapper<SetSharedResourceLikeMapperRequest, Boolean>
    {
        /**
         * The request last passed into excecute.
         */
        private SetSharedResourceLikeMapperRequest request;

        /**
         * execute.
         * 
         * @param inRequest
         *            the request made - store it
         * @return true
         */
        @Override
        public Boolean execute(final SetSharedResourceLikeMapperRequest inRequest)
        {
            request = inRequest;
            return true;
        }

        /**
         * @return the request
         */
        public SetSharedResourceLikeMapperRequest getRequest()
        {
            return request;
        }

        /**
         * @param inRequest
         *            the request to set
         */
        public void setRequest(final SetSharedResourceLikeMapperRequest inRequest)
        {
            request = inRequest;
        }
    }

    /**
     * Fake mapper to store the SharedResourceRequest so the tests can verify it was made properly.
     */
    private class SharedResourceRequestMapperFake implements DomainMapper<SharedResourceRequest, SharedResource>
    {
        /**
         * The request last passed into excecute.
         */
        private SharedResourceRequest request;

        /**
         * The shared resource that's returned by the find or insert mapper.
         */
        private SharedResource returnedResource;

        /**
         * execute.
         * 
         * @param inRequest
         *            the request made - store it
         * @return true
         */
        @Override
        public SharedResource execute(final SharedResourceRequest inRequest)
        {
            request = inRequest;
            return returnedResource;
        }

        /**
         * @return the request
         */
        public SharedResourceRequest getRequest()
        {
            return request;
        }

        /**
         * @param inRequest
         *            the request to set
         */
        public void setRequest(final SharedResourceRequest inRequest)
        {
            request = inRequest;
        }

        /**
         * @return the returnedResource
         */
        public SharedResource getReturnedResource()
        {
            return returnedResource;
        }

        /**
         * @param inReturnedResource
         *            the returnedResource to set
         */
        public void setReturnedResource(final SharedResource inReturnedResource)
        {
            returnedResource = inReturnedResource;
        }
    }
}
