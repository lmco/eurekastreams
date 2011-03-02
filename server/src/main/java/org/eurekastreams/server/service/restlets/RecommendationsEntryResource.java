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
package org.eurekastreams.server.service.restlets;

import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Recommendation;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/** 
 * Class RecommendationEntryResource.
 */
public class RecommendationsEntryResource extends RecommendationsResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(RecommendationsEntryResource.class);

    /**
     * Open Social Id of the subject of the recommendation.
     */
    private String openSocialId;

    /**
     * Id of this recommendation.
     * 
     */
    private Long recommendationId;


    /**
     * Initialized the paremeters from the Request object.
     * @param request
     * 			the request object
     */
    @Override
    protected void initParams(final Request request)
    {
        openSocialId = (String) request.getAttributes().get("openSocialId");
        recommendationId = Long.valueOf((String) request.getAttributes().get("recommendationId"));
    }

    /**
     * Handle DELETE requests.
     * @throws ResourceException  throws a ResourceException
     */
    @Override
    public void removeRepresentations() throws ResourceException
    {
        log.debug("RecommendationsEntryResource: DELETE " + openSocialId);
        getRecommendationMapper().delete(recommendationId);
    }

    /**
     * Handle GET requests.
     * 
     * @param variant
     *            the variant
     * @throws ResourceException  throws a ResourceException
     * @return a representation of this resource. 
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        log.debug("Attempting to GET recommendation by Id: " + recommendationId);
        Recommendation reco = getRecommendationMapper().findById(recommendationId);
        
        if (null == reco)
        {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
        
        Map<String, Person> people = getPeopleInfoForRecommendations(reco);
        
        JSONObject jsonReco = convertRecoToJSON(reco, 
                people.get(reco.getAuthorOpenSocialId()),
                people.get(reco.getSubjectOpenSocialId()));
        
        Representation rep = new StringRepresentation(jsonReco.toString(), MediaType.APPLICATION_JSON); 
        
        rep.setExpirationDate(new Date(0L));
        
        return rep;
    }
}
