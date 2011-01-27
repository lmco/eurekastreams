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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Gets a list of Person objects for a given list of person ids.
 */
public class GetPeopleByOpenSocialIds extends GetItemsByPointerIds<PersonModelView>
{
    /**
     * Bulk mapper.
     */
    private GetPeopleByIds bulkPeopleMapper;

    /**
     * Sets the bulk mapper.
     * 
     * @param inBulkPeopleMapper
     *            the bulk mapper.
     */
    public void setBulkPeopleMapper(final GetPeopleByIds inBulkPeopleMapper)
    {
        bulkPeopleMapper = inBulkPeopleMapper;
    }

    /**
     * Execute the bulk mapper.
     * 
     * @param ids
     *            the list of ids.
     * @return the list of model views.
     */
    @Override
    protected List<PersonModelView> bulkExecute(final List<Long> ids)
    {
        return bulkPeopleMapper.execute(ids);
    }
}
