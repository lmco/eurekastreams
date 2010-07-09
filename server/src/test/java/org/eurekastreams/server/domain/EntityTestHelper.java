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
package org.eurekastreams.server.domain;

/**
 * Helper for use with unit tests.
 */
public final class EntityTestHelper
{
    /**
     * Private default constructor.
     */
    private EntityTestHelper()
    {
        // -no-op
    }

    /**
     * Set the id of the input person - for test purposes only.
     * 
     * @param inPerson
     *            the person to set id for
     * @param id
     *            the id to give the person
     */
    public static void setPersonId(final Person inPerson, final Long id)
    {
        inPerson.setId(id);
    }

    /**
     * Set the parent organization id of the input person.
     * 
     * @param inPerson
     *            the person to set the parent org id
     * @param inParentOrgId
     *            the org id to set as the parent org id
     */
    public static void setPersonParentOrgId(final Person inPerson, final Long inParentOrgId)
    {
        inPerson.setParentOrgId(inParentOrgId);
    }
}
