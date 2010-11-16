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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.TutorialVideo;
import org.eurekastreams.server.domain.TutorialVideoDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.service.actions.strategies.TutorialVideoDTOFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

/**
 * Get a set of tutorial videos.
 * 
 */
public class GetTutorialVideos extends CachedDomainMapper implements DomainMapper<Long, Set<TutorialVideoDTO>>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Gets Tutorial Videos.
     * 
     * @param inRequest
     *            ignored value.
     * 
     * @return a Set of TutorialVideoDTO.
     */
    @Override
    public HashSet<TutorialVideoDTO> execute(final Long inRequest)
    {
        HashSet<TutorialVideoDTO> result = getVidsFromCache();
        return result == null ? setInCache(getVidsFromDB()) : result;
    }

    /**
     * Get PersonPagePropertiesDTO from cache if present.
     * 
     * @return Set<TutorialVideoDTO> from cache if present, null otherwise.
     */
    private HashSet<TutorialVideoDTO> getVidsFromCache()
    {
        return (HashSet<TutorialVideoDTO>) getCache().get(CacheKeys.TUTORIAL_VIDS);
    }

    /**
     * Sets HashSet<TutorialVideoDTO> into cache.
     * 
     * @param inVids
     *            Vids to store in cache.
     * @return HashSet<TutorialVideoDTO>.
     */
    private HashSet<TutorialVideoDTO> setInCache(final HashSet<TutorialVideoDTO> inVids)
    {
        log.debug("Setting " + CacheKeys.TUTORIAL_VIDS + " in cache.");
        getCache().set(CacheKeys.TUTORIAL_VIDS, inVids);
        return inVids;
    }

    /**
     * Gets Tutorial Videos from db..
     * 
     * @return a Set of TutorialVideoDTO.
     */
    @SuppressWarnings("unchecked")
    public HashSet<TutorialVideoDTO> getVidsFromDB()
    {
        log.debug("Unable to locate " + CacheKeys.TUTORIAL_VIDS + " in cache, going to DB");
        Criteria criteria = getHibernateSession().createCriteria(TutorialVideo.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(getColumn("page"));
        fields.add(getColumn("dialogTitle"));
        fields.add(getColumn("innerContentTitle"));
        fields.add(getColumn("innerContent"));
        fields.add(getColumn("videoUrl"));
        fields.add(getColumn("videoWidth"));
        fields.add(getColumn("videoHeight"));
        criteria.setProjection(fields);
        ModelViewResultTransformer<TutorialVideoDTO> resultTransformer = //
        new ModelViewResultTransformer<TutorialVideoDTO>(new TutorialVideoDTOFactory());
        criteria.setResultTransformer(resultTransformer);
        List<TutorialVideoDTO> tutorialVideoSet = criteria.list();

        return new HashSet<TutorialVideoDTO>(tutorialVideoSet);
    }
}
