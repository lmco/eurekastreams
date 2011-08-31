/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.junit.Test;

/**
 * Tests ExcludeItemsTrimmerFactory.
 */
public class ExcludeItemsTrimmerFactoryTest
{
    /**
     * The core of the test.
     *
     * @param json
     *            String of the JSON request.
     * @param excludeIds
     *            IDs to be excluded via a trimmer; null for no exclusion.
     */
    private void coreTest(final String json, final List<Long> excludeIds)
    {
        JSONObject request = JSONObject.fromObject(json);

        ExcludeItemsTrimmerFactory sut = new ExcludeItemsTrimmerFactory();
        ListTrimmer trimmer = sut.getTrimmer(request, null);

        if (excludeIds != null && trimmer instanceof ExcludeItemsTrimmer)
        {
            // use reflection to get the list (brittle, but the most straightforward way to do this)
            try
            {
                Field field = trimmer.getClass().getDeclaredField("idsToExclude");
                field.setAccessible(true);
                List<Long> checkIds = (List<Long>) field.get(trimmer);
                assertEquals(excludeIds, checkIds);
            }
            catch (SecurityException ex)
            {
                Assert.fail("Test obsolete. " + ex);
            }
            catch (NoSuchFieldException ex)
            {
                Assert.fail("Test obsolete. " + ex);
            }
            catch (IllegalArgumentException ex)
            {
                Assert.fail(ex.toString());
            }
            catch (IllegalAccessException ex)
            {
                Assert.fail(ex.toString());
            }
        }
        else if (!(excludeIds == null && trimmer instanceof NullTrimmer))
        {
            Assert.fail("Unexpected trimmer type " + trimmer.getClass());
        }
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmerEmptyRequest()
    {
        coreTest("{}", null);
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmerNoExcludeBlock()
    {
        coreTest("{query:{}}", null);
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmerEmptyExcludeBlock()
    {
        coreTest("{query:{},exclude:{}}", null);
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmerExtraneousExcludeBlock()
    {
        coreTest("{query:{},exclude:{else:{}}}", null);
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmerWrongTypeObject()
    {
        coreTest("{query:{},exclude:{ids:{}}}", null);
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmerWrongTypeString()
    {
        coreTest("{query:{},exclude:{ids:'no'}}", null);
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmerEmptyArray()
    {
        coreTest("{query:{},exclude:{ids:[]}}", null);
    }

    /**
     * Tests factory.
     */
    @Test
    public void testGetTrimmer()
    {
        coreTest("{query:{},exclude:{ids:['A',1,{},2,3,[],[4,5],6,'7']}}", Arrays.asList(1L, 2L, 3L, 6L, 7L));
    }
}
