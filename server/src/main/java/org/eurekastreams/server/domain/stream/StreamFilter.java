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


/**
 * Interface to use for Stream objects (e.g. StreamSearch, StreamView)
 * so they can be handled the same by client side.
 *
 */
public interface StreamFilter
{
    /**
     * Return the  name of the object.
     * @return The name of the object. 
     */
    String getName();
    
    /**
     * Set the name of the StreamFilter.
     * @param inName The name of the StreamFilter.
     */
    void setName(String inName);
    
    /**
     * Return the request.
     * @return the request.
     */
    String getRequest();
    
    /**
     * Get the id of the StreamFilter.
     * @return the id.
     */
    long getId();
}
