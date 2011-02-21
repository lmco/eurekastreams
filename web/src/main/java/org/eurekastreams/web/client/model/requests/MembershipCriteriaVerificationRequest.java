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
package org.eurekastreams.web.client.model.requests;

import java.io.Serializable;

/**
 * Request to verify validity of membership criteria.
 */
public class MembershipCriteriaVerificationRequest implements Serializable
{
    /** Actual criteria. */
    private final String criteria;

    /** If group or attribute query. */
    private final boolean isGroup;

    /**
     * Constructor.
     *
     * @param inCriteria
     *            Actual criteria.
     * @param inIsGroup
     *            If group or attribute query.
     */
    public MembershipCriteriaVerificationRequest(final String inCriteria, final boolean inIsGroup)
    {
        criteria = inCriteria;
        isGroup = inIsGroup;
    }

    /**
     * @return the criteria
     */
    public String getCriteria()
    {
        return criteria;
    }

    /**
     * @return the isGroup
     */
    public boolean isGroup()
    {
        return isGroup;
    }
}
