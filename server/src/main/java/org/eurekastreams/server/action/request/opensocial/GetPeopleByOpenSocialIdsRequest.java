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
package org.eurekastreams.server.action.request.opensocial;

import java.io.Serializable;
import java.util.List;

/**
 * Request object for the GetPeopleByOpenSocialIds action.
 *
 */
public class GetPeopleByOpenSocialIdsRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -7689447039426297355L;

    /**
     * Local instance of the List of OpenSocialIds.
     */
    private List<String> openSocialIds;

    /**
     * Local instance of the String of Type of Relationships for People to return.
     */
    private String typeOfRelationshipForPeopleReturned;

    /**
     * Empty constructor for EJB Serialization compliance.
     */
    public GetPeopleByOpenSocialIdsRequest()
    {
        // Empty constructor for EJB compliance.
    }

    /**
     * Constructor.
     *
     * @param inOpenSocialIds
     *            - {@link List} of OpenSocialIds for the request.
     * @param inTypeOfRelationshipForPeopleReturned
     *            - String based descriptor of the type of relationships of people being requested.
     */
    public GetPeopleByOpenSocialIdsRequest(final List<String> inOpenSocialIds,
            final String inTypeOfRelationshipForPeopleReturned)
    {
        openSocialIds = inOpenSocialIds;

        typeOfRelationshipForPeopleReturned = inTypeOfRelationshipForPeopleReturned;
    }

    /**
     * @return the openSocialIds
     */
    public List<String> getOpenSocialIds()
    {
        return openSocialIds;
    }

    /**
     * @param inOpenSocialIds
     *            the openSocialIds to set
     */
    public void setOpenSocialIds(final List<String> inOpenSocialIds)
    {
        this.openSocialIds = inOpenSocialIds;
    }

    /**
     * @return the typeOfRelationshipForPeopleReturned
     */
    public String getTypeOfRelationshipForPeopleReturned()
    {
        return typeOfRelationshipForPeopleReturned;
    }

    /**
     * @param inTypeOfRelationshipForPeopleReturned
     *            the typeOfRelationshipForPeopleReturned to set
     */
    public void setTypeOfRelationshipForPeopleReturned(final String inTypeOfRelationshipForPeopleReturned)
    {
        this.typeOfRelationshipForPeopleReturned = inTypeOfRelationshipForPeopleReturned;
    }
}
