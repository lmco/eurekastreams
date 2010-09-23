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
package org.eurekastreams.server.persistence.mappers.requests;

import java.io.Serializable;

/**
 * Request for GetRelatedEntityCount mapper.
 * 
 */
public class GetRelatedEntityCountRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 4203441833444382041L;

    /**
     * Related entity name.
     */
    private String relatedEntityName;

    /**
     * Db id of target entity to count related entities for.
     */
    private Long targetEntityId;

    /**
     * Field name of target entity in related entity.
     */
    private String targetEntityFieldName;

    /**
     * Optional where clause addtion. (begin with AND).
     */
    private String whereClauseAddition;

    /**
     * Constructor.
     * 
     * @param inRelatedEntityName
     *            Related entity name.
     * @param inTargetEntityFieldName
     *            Field name of target entity in related entity.
     * @param inTargetEntityId
     *            Db id of target entity to count related entities for.
     * @param inWhereClauseAddition
     *            Optional where clause addtion. (begin with AND).
     */
    public GetRelatedEntityCountRequest(final String inRelatedEntityName, final String inTargetEntityFieldName,
            final Long inTargetEntityId, final String inWhereClauseAddition)
    {
        relatedEntityName = inRelatedEntityName;
        targetEntityFieldName = inTargetEntityFieldName;
        targetEntityId = inTargetEntityId;
        whereClauseAddition = inWhereClauseAddition;
    }

    /**
     * Constructor.
     * 
     * @param inRelatedEntityName
     *            Related entity name.
     * @param inTargetEntityFieldName
     *            Field name of target entity in related entity.
     * @param inTargetEntityId
     *            Db id of target entity to count related entities for.
     */
    public GetRelatedEntityCountRequest(final String inRelatedEntityName, final String inTargetEntityFieldName,
            final Long inTargetEntityId)
    {
        this(inRelatedEntityName, inTargetEntityFieldName, inTargetEntityId, null);
    }

    /**
     * @return the relatedEntityName
     */
    public String getRelatedEntityName()
    {
        return relatedEntityName;
    }

    /**
     * @return the targetEntityId
     */
    public Long getTargetEntityId()
    {
        return targetEntityId;
    }

    /**
     * @return the targetEntityFieldName
     */
    public String getTargetEntityFieldName()
    {
        return targetEntityFieldName;
    }

    /**
     * @return the whereClauseAddition
     */
    public String getWhereClauseAddition()
    {
        return whereClauseAddition;
    }

}
