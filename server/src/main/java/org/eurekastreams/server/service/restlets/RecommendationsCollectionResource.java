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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Recommendation;

/**
 * Represents a collection of recommendations about a person.
 */
public class RecommendationsCollectionResource extends RecommendationsResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(RecommendationsCollectionResource.class);

    /**
     * The open social id of the subject of the recommendations.
     */
    private String openSocialId;
    
    /**
     * Maximum number of results to return in the request.
     */
    private int maxResults = 0;

    /**
     * Initialize the resource.
     * 
     * @param request
     *            the request.
     */
    @Override
    protected void initParams(final Request request)
    {
        log.debug("Initializing params for Recommendation");
        openSocialId = (String) request.getAttributes().get("openSocialId");
        if (request.getAttributes().containsKey("maxResults"))
        {
            try
            {
                String tempMaxResults = (String) request.getAttributes().get("maxResults"); 
                log.debug("Retreived maxResults from querystring: " + tempMaxResults);
                maxResults = Integer.parseInt(tempMaxResults);
            }
            catch (Exception ex)
            {
                maxResults = 0;
            }
        }
    }

    /**
     * Responds to HTTP Request.
     * 
     * @param variant
     *            determines response.
     * @throws ResourceException
     *             thrown when unable to represent.
     * 
     * @return the representation.
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        log.debug("Getting recommendations for user with OS ID: " + openSocialId);
        List<Recommendation> recommendations; 
        if (maxResults > 0)
        {
            recommendations = getRecommendationMapper().findBySubjectOpenSocialId(openSocialId, maxResults);
        }
        else
        {
            recommendations = getRecommendationMapper().findBySubjectOpenSocialId(openSocialId);
        }

        log.debug(recommendations.size() + " recommendations found");

        JSONObject json = new JSONObject();

        JSONArray recos = new JSONArray();

        if (null != recommendations && recommendations.size() > 0)
        {
            Map<String, Person> people = getPeopleInfoForRecommendations(recommendations);
            log.debug("There are " + people.size() + " Person objects available for recommendations.");
            
            for (Recommendation reco : recommendations)
            {
                recos.add(convertRecoToJSON(reco, 
                        people.get(reco.getAuthorOpenSocialId()), 
                        people.get(reco.getSubjectOpenSocialId())));
            }
        }

        json.put(RECOMMENDATIONS_KEY, recos);

        Representation rep = new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON); 
        
        rep.setExpirationDate(new Date(0L));
        
        return rep;                
    }

    /**
     * Accept a POST request.
     * 
     * @param entity
     *            the representation of a new entry.
     * 
     * @throws ResourceException
     *             thrown when unable to accept representation.
     */
    @Override
    public void acceptRepresentation(final Representation entity) throws ResourceException
    {
        String json;
        try
        {
            json = entity.getText();
            log.debug("RecommendationsCollectionResource POST" + json);

            JSONObject jsonReco = JSONObject.fromObject(json);
            JSONObject jsonAuthor = jsonReco.getJSONObject(AUTHOR_KEY);
            JSONObject jsonSubject = jsonReco.getJSONObject(SUBJECT_KEY);
            Recommendation recommendation = new Recommendation(jsonSubject.getString(ID_KEY), 
                    jsonAuthor.getString(ID_KEY), jsonReco.getString(TEXT_KEY));

            getRecommendationMapper().insert(recommendation);
            
            Map<String, Person> people = getPeopleInfoForRecommendations(recommendation);
            
            JSONObject recoJSON = convertRecoToJSON(recommendation, 
                    people.get(recommendation.getAuthorOpenSocialId()),
                    people.get(recommendation.getSubjectOpenSocialId()));
            
            getAdaptedResponse().setEntity(recoJSON.toString(), 
                    MediaType.APPLICATION_JSON);
        }
        catch (IOException e)
        {
            log.error("POST to RecommendationsCollection failed", e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }

        log.debug("RecommendationsCollectionResource POST " + entity.toString());
    }

}
