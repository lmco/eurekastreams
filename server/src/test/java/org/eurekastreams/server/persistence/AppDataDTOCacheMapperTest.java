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
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.domain.dto.AppDataDTO;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for AppDataDTOCacheMapper.
 */
public class AppDataDTOCacheMapperTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test findOrCreateByPersonAndGadgetDefinitionIds when exists in cache.
     */
    @Test
    public void testFindOrCreateWhenInCache()
    {
        final Cache cache = context.mock(Cache.class);
        final AppDataMapper dbMapper = context.mock(AppDataMapper.class);
        final long defId = 2821L;
        final String openSocialId = "sldkfjl";

        final AppDataDTO expectedResult = new AppDataDTO(openSocialId, defId, new HashMap<String, String>());

        context.checking(new Expectations()
        {
            {
                oneOf(cache).get(
                        CacheKeys.APPDATA_BY_GADGET_DEFINITION_ID_AND_UNDERSCORE_AND_PERSON_OPEN_SOCIAL_ID + defId
                                + "_" + openSocialId);
                will(returnValue(expectedResult));
            }
        });

        AppDataDTOCacheMapper sut = new AppDataDTOCacheMapper(dbMapper);
        sut.setCache(cache);

        AppDataDTO result = sut.findOrCreateByPersonAndGadgetDefinitionIds(defId, openSocialId);
        assertSame(expectedResult, result);
    }

    /**
     * Test findOrCreateByPersonAndGadgetDefinitionIds when exists in cache.
     */
    @Test
    public void testFindOrCreateWhenNotInCache()
    {
        final Cache cache = context.mock(Cache.class);
        final AppDataMapper dbMapper = context.mock(AppDataMapper.class);
        final long defId = 2821L;
        final String openSocialId = "sldkfjl";
        final AppData expectedResult = context.mock(AppData.class);
        final Map<String, String> keyValuePairs = new HashMap<String, String>();

        final String key = CacheKeys.APPDATA_BY_GADGET_DEFINITION_ID_AND_UNDERSCORE_AND_PERSON_OPEN_SOCIAL_ID + defId
                + "_" + openSocialId;

        context.checking(new Expectations()
        {
            {
                oneOf(cache).get(key);
                will(returnValue(null));

                oneOf(dbMapper).findOrCreateByPersonAndGadgetDefinitionIds(defId, openSocialId);
                will(returnValue(expectedResult));

                oneOf(expectedResult).getValues();
                will(returnValue(keyValuePairs));

                oneOf(cache).set(with(key), with(any(AppDataDTO.class)));
            }
        });

        AppDataDTOCacheMapper sut = new AppDataDTOCacheMapper(dbMapper);
        sut.setCache(cache);

        AppDataDTO result = sut.findOrCreateByPersonAndGadgetDefinitionIds(defId, openSocialId);
        assertEquals(openSocialId, result.getOpenSocialId());
        assertEquals(defId, result.getGadgetDefinitionId());
        assertSame(keyValuePairs, result.getKeyValuePairs());
    }
}
