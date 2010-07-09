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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.Image;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Delete image test.
 *
 */
public class DeleteImageTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteImage sut;

    /**
     * Insert mapper.
     */
    @Autowired
    private InsertMapper<Image> insertMapper;

    /**
     * Get mapper.
     */
    @Autowired
    private GetImageByIdentifier getMapper;


    /**
     * Insert it into the DB. Can't do this in the dataset because of the blob.
     */
    @Before
    public void before()
    {
        Image insertedImage = new Image("myImage", new byte[5]);
        insertMapper.execute(new PersistenceRequest<Image>(insertedImage));
    }

    /**
     * Get. Delete. Get. Confirm.
     */
    @Test
    public void executeTest()
    {
        Image myImage = getMapper.execute("myImage");
        assertTrue(myImage != null);

        sut.execute("myImage");

        Image myDeletedImage = getMapper.execute("myImage");
        assertTrue(myDeletedImage == null);
    }
}
