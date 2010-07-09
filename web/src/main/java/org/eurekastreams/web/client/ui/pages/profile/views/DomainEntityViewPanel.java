/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.profile.views;


/**
 * This interface covers both types of pages that will be displayed on the profile page: ProfilePanel and
 * ProfileSettingsPanel.
 *
 * @param <T>
 *            the type of domain entity.
 */
public interface DomainEntityViewPanel<T>
{
    /**
     * Update the panel with a person to display.
     *
     * @param entity
     *            the new entity.
     */
    void setEntity(T entity);

    /**
     * Update the display to reflect the error that happened while trying to get the entity.
     *
     * @param caught
     *            the exception
     */
    void handleRefreshEntityFailure(Throwable caught);

    /**
     * If the user is authenticated.
     *
     * @param value
     *            true/false.
     */
    void setIsAuthenticated(boolean value);
}
