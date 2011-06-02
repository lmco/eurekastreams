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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Transformer to create MembershipCriteria persistence request from action context containing MembershipContextDTO.
 */
public class ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer implements
        Transformer<ActionContext, PersistenceRequest<MembershipCriteria>>
{

    /**
     * create MembershipCriteria persistence request from action context containing MembershipContextDTO.
     * 
     * @param inTransformType
     *            ActionContext containing MembershipCriteriaDTO.
     * @return PersistenceRequest for MembershipCriteria.
     */
    @Override
    public PersistenceRequest<MembershipCriteria> transform(final ActionContext inTransformType)
    {
        MembershipCriteriaDTO mcdto = (MembershipCriteriaDTO) inTransformType.getParams();
        MembershipCriteria mc = new MembershipCriteria();
        mc.setCriteria(mcdto.getCriteria());

        return new PersistenceRequest<MembershipCriteria>(mc);
    }

}
