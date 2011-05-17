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
package org.eurekastreams.server.action.execution.gallery;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.junit.Test;

/**
 * Test for GalleryTabTemplateFactoryTest.
 * 
 */
public class GalleryTabTemplateFactoryTest
{
    /**
     * System under test.
     */
    private GalleryTabTemplateFactory sut;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        sut = new GalleryTabTemplateFactory();
        assertTrue(sut.execute(null) instanceof GalleryTabTemplate);

    }
}
