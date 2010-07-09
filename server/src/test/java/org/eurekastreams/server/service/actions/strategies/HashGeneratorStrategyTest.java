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
package org.eurekastreams.server.service.actions.strategies;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the HashGeneratorStrategy.
 */
public class HashGeneratorStrategyTest
{
    /**
     * This is a hard class to test. All it does is randomly generate hashes. Im going to test it by feeding in the same
     * input string and making sure the two results are different.
     */
    @Test
    public void hash()
    {
        String input1 = "somekey";
        HashGeneratorStrategy hasher = new HashGeneratorStrategy();
        String output1 = hasher.hash(input1);
        String output2 = hasher.hash(input1);
        Assert.assertNotSame(output1, output2);
    }
}
