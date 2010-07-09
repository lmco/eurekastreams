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
package org.eurekastreams.server.domain.stream;

import javax.persistence.Entity;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Stream item - used to contain an auto-incrementing stream id for the different entities which are considered stream
 * items. Entities which are stream items need to contain a reference to their parent stream id.
 */
@Entity
public class StreamItemId extends DomainEntity
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = -7174129385078724160L;
}
