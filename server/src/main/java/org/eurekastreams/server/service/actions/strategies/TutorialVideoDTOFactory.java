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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.commons.hibernate.ModelViewFactory;
import org.eurekastreams.server.domain.TutorialVideoDTO;

/**
 * Factory to build Tutorial Video DTOs.
 */
public class TutorialVideoDTOFactory extends ModelViewFactory<TutorialVideoDTO>
{
    /**
     * Build a new Tutorial Video DTO.
     * 
     * @return a new Tutorial Video DTO
     */
    @Override
    public TutorialVideoDTO buildModelView()
    {
        return new TutorialVideoDTO();
    }
}
