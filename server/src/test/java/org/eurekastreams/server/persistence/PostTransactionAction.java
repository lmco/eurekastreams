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
package org.eurekastreams.server.persistence;

/**
 * Test methods can create an inner class
 * that implements this interface in order
 * to take advantage of JUnit's @PostTransaction
 * attribute without interfering with other tests.
 * See JpaTabaMapperTest.TestGadgetDefinition method
 * for example.
 *
 */
public interface PostTransactionAction
{
    /**
     * This is the method that the @PostTransaction
     * method will execute. Test specific behavior
     * should be implemented here.
     */
    void execute();
}
