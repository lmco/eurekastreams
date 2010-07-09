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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.EmptyRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the get all mappers plugin.
 *
 */
public class GetAllPluginsMapperTest  extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetAllPluginsMapper sut;
   

    /**
     * Test execute on existing feed.
     */
    @Test
    public void execute()
    {
        List<PluginDefinition> plugins = sut.execute(new EmptyRequest());
        assertEquals(3, plugins.size());
    }
}
