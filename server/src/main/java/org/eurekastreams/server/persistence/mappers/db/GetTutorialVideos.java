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

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.TutorialVideo;
import org.eurekastreams.server.domain.TutorialVideoDTO;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.service.actions.strategies.TutorialVideoDTOFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

/**
 * Get a list of tutorial videos ids straight from the database.
 * 
 */
public class GetTutorialVideos extends ReadMapper<Long, Set<TutorialVideoDTO>>
{
    /**
     * Gets Tutorial Videos.
     * 
     * @param inTutorialVideoId
     *            value is not used.
     * @return a Set of TutorialVideoDTO.
     */
    @SuppressWarnings("unchecked")
    @Override
    public HashSet<TutorialVideoDTO> execute(final Long inTutorialVideoId)
    {
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
        ModelViewResultTransformer<TutorialVideoDTO> resultTransformer = 
            new ModelViewResultTransformer<TutorialVideoDTO>(
                new TutorialVideoDTOFactory());
        criteria.setResultTransformer(resultTransformer);
        List<TutorialVideoDTO> tutorialVideoSet = criteria.list();

        return new HashSet<TutorialVideoDTO>(tutorialVideoSet);
    }

}
