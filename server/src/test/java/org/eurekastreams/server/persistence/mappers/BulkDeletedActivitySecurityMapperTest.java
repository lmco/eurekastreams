/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.junit.Test;

/**
 * Tests bulk deleted activity security mapper.
 */
public class BulkDeletedActivitySecurityMapperTest
{
    /**
     * System under test.
     */
    private BulkDeletedActivitySecurityMapper sut = new BulkDeletedActivitySecurityMapper();

    /**
     * Tests execution.
     */
    @Test
    public void testExecute()
    {
        List<Long> request = Arrays.asList(1L, 2L, 3L);

        List<ActivitySecurityDTO> results = sut.execute(request);
        
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(Boolean.FALSE, results.get(0).getExists());
        Assert.assertEquals(Boolean.FALSE, results.get(1).getExists());
        Assert.assertEquals(Boolean.FALSE, results.get(2).getExists());

        Assert.assertEquals(new Long(1L), results.get(0).getId());
        Assert.assertEquals(new Long(2L), results.get(1).getId());
        Assert.assertEquals(new Long(3L), results.get(2).getId());
    }
}
