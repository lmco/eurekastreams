/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for TabGroup.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class DomainEntityTest
{
    /**
     * Basic test to ensure the id property works properly.
     */
    @Test
    public void testIdProperty()
    {
        long id = 1;        
        DomainEntitySubClassSupport domainEntity = new DomainEntitySubClassSupport();

        domainEntity.setId(id);
        
        assertEquals(
                "getId() doesn't return the same value as the previous setId()",
                id, domainEntity.getId());
    }
    
    /**
     * Basic test to ensure the version works properly.
     */
    @Test
    public void testVersionProperty()
    {
        long version = 1;        
        DomainEntitySubClassSupport domainEntity = new DomainEntitySubClassSupport();

        domainEntity.setVersion(version);
        
        assertEquals(
                "getVersion() doesn't return the same value as the previous setVersion()",
                version, domainEntity.getVersion());
    }
    
}
