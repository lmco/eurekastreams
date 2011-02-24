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
package org.eurekastreams.server.domain;

/**
 * Interface for entity cache updaters.
 * 
 * @param <T>
 *            the type of entity managed
 */
public interface EntityCacheUpdater<T extends net.sf.gilead.pojo.gwt.LightEntity>
{
    /**
     * Called on an entity that has just been updated.
     * 
     * @param entity
     *            the entity being updated
     */
    void onPostUpdate(final T entity);

    /**
     * Called on an entity that has just been persisted.
     * 
     * @param entity
     *            the entity being persisted
     */
    void onPostPersist(final T entity);
}
