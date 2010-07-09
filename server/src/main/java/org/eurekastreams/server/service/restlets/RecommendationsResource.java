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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.restlet.data.Request;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Recommendation;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.RecommendationMapper;

/**
 * Abstract class that contains the commonalities between the Entry and Collection
 * endpoints for Recommendations.
 *
 */
public abstract class RecommendationsResource extends WritableResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(RecommendationsResource.class);
    
    /**
     * Mapper for getting recommendations.
     */
    private RecommendationMapper recommendationMapper;
    
    /**
     * Mapper for getting people.
     */
    private PersonMapper personMapper;

    /**
     * The JSON objects key.
     */
    public static final String RECOMMENDATIONS_KEY = "recommendations";

    /**
     * JSON field for author.
     */
    public static final String AUTHOR_KEY = "author";

    /**
     * JSON Key for Author Name.
     */
    public static final String AUTHOR_NAME_KEY = "authorName";
    
    /**
     * JSON Key for person image.
     */
    public static final String PERSON_IMAGE_KEY = "personImage";

    /**
     * JSON field for subject.
     */
    public static final String SUBJECT_KEY = "subject";

    /**
     * JSON field for the recommendation text.
     */
    public static final String TEXT_KEY = "text";

    /**
     * JSON field for the date.
     */
    public static final String DATE_KEY = "date";

    /**
     * Date format for SimpleDateFormat input.
     */
    public static final String DATE_FORMAT = "M/d/yy";
    
    /**
     * JSON field for the id.
     */
    public static final String ID_KEY = "id";
    
    /**
     * Pass this abstract method onto subclasses for implementations.
     * @param request - request that is handled by the restlet.
     */
    protected abstract void initParams(Request request);

    /**
     * Getter for the recommendation mapper.
     * @return recommendation mapper.
     */
    public RecommendationMapper getRecommendationMapper()
    {
        return recommendationMapper;
    }
    
    /**
     * Setter for the recommendation mapper.
     * @param inMapper - instance of the mapper.
     */
    public void setRecommendationMapper(final RecommendationMapper inMapper)
    {
        recommendationMapper = inMapper;
    }
    
    /**
     * Getter for the person mapper.
     * @return person mapper.
     */
    public PersonMapper getPersonMapper()
    {
        return personMapper;
    }
    
    /**
     * Setter for the person mapper.
     * @param inMapper - instance of the mapper.
     */
    public void setPersonMapper(final PersonMapper inMapper)
    {
        personMapper = inMapper;
    }
    
    /**
     * This method converts a recommendation to JSON.
     * @param inReco - recommendation object.
     * @param inAuthor - Person object representing the author of the recommendation.
     * @param inSubject - Person object representing the subject of the recommendation.
     * @return - JSONObject representing the recommendation object.
     */
    protected JSONObject convertRecoToJSON(final Recommendation inReco, final Person inAuthor, final Person inSubject)
    {
        JSONObject jsonReco = new JSONObject();
        jsonReco.put(ID_KEY, inReco.getId());
        jsonReco.put(AUTHOR_KEY, convertPersonToRecoJSONPerson(inAuthor));
        jsonReco.put(SUBJECT_KEY, convertPersonToRecoJSONPerson(inSubject));
        
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        jsonReco.put(DATE_KEY, dateFormat.format(inReco.getDate()).toString());

        jsonReco.put(TEXT_KEY, inReco.getText());
        return jsonReco;
    }
    
    /**
     * This method converts a person object into JSON for the purposes of the Recommendation object.
     * @param inPerson - person object to conver to JSON.
     * @return - JSON representation of the passed in person object.
     */
    protected JSONObject convertPersonToRecoJSONPerson(final Person inPerson)
    {
        JSONObject person = new JSONObject();
        
        person.put(AUTHOR_NAME_KEY, inPerson.getDisplayName());
        
        AvatarUrlGenerator generator = new AvatarUrlGenerator(EntityType.PERSON);
        person.put(PERSON_IMAGE_KEY, generator.getSmallAvatarUrl(inPerson.getId(), inPerson.getAvatarId()));
        
        person.put(ID_KEY, inPerson.getOpenSocialId());
        
        return person;
    }

    /**
     * Retrieve the Person objects for the list of opensocial ids passed in using the List.
     * @param inOpenSocialIds - List of opensocial ids to retrieve person objects for.
     * @return - Map of person objects with the key being the opensocial id.
     */
    private Map<String, Person> getPeopleMapByOpenSocialIds(final List<String> inOpenSocialIds)
    {
        log.debug("Retrieve people for the opensocialIds passed in, there were: " + inOpenSocialIds.size() 
                + " ids passed in.");
        Map<String, Person> people = new HashMap<String, Person>();
        log.debug("Retrieving from the mapper now.");
        List<Person> requestedPeople = getPersonMapper().findPeopleByOpenSocialIds(inOpenSocialIds);
        log.debug("Found the people (" + requestedPeople.size() + " of them), now creating the map");
        for (Person currentPerson : requestedPeople)
        {
            people.put(currentPerson.getOpenSocialId(), currentPerson);
            log.debug("Putting the currentPerson: osId" + currentPerson.getOpenSocialId() + " onto the map");
        }
        log.debug("Map created, returning.");
        return people;
    }
  
    /**
     * Retrieve a Map of the people for a single recommendation with their opensocial id as the key.
     * @param inReco - Recommendation to retrieve the people objects for.
     * @return - Map of People objects for the Recommendation keyed by their corresponding
     *          opensocial id.
     */
    protected Map<String, Person> getPeopleInfoForRecommendations(final Recommendation inReco)
    {
        List<String> osIds = new ArrayList<String>();
        osIds.add(inReco.getAuthorOpenSocialId());
        osIds.add(inReco.getSubjectOpenSocialId());
        log.debug("Get People for Recommendation, author:" + inReco.getAuthorOpenSocialId() 
                + ", subject: " + inReco.getSubjectOpenSocialId());
        return getPeopleMapByOpenSocialIds(osIds);
    }
    
    /**
     * Retrieve a Map of the people for a list of recommendations with their opensocial id as the key.
     * @param inRecos - List of Recommendations to retrieve the people objects for.
     * @return - Map of People objects for the Recommendations keyed by their corresponding
     *          opensocial id.
     */
    protected Map<String, Person> getPeopleInfoForRecommendations(final List<Recommendation> inRecos)
    {
        if (inRecos.size() > 0)
        {
            List<String> osIds = new ArrayList<String>();
            osIds.add(inRecos.get(0).getSubjectOpenSocialId());
            for (Recommendation reco : inRecos)
            {
                osIds.add(reco.getAuthorOpenSocialId());
            }
            
            return getPeopleMapByOpenSocialIds(osIds);
        }
        else
        {
            return null;
        }
    }
}
