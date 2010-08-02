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
package org.eurekastreams.server.persistence.mappers;

/**
 * Interface used in chaining datasources - if a datasource can't find the data, but one downstream can, an
 * implementation of this interface is responsible for updating that datasource with the data found downstream.
 *
 * @param <DataSourceType>
 *            the type of data this data source deals with
 */
public interface RefreshDataSourceMapper<DataSourceType>
{
    /**
     * Refresh the data source with the input data.
     *
     * @param data
     *            the data to update the data source with
     */
    void refresh(final DataSourceType data);
}
