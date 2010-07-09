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
package org.eurekastreams.server.service.restlets;

import org.springframework.transaction.annotation.Transactional;

/**
 * Parent class for our Resources that are to be writable. The @Transactional annotation needs to be on the handlePut()
 * and handlePost() methods. Having this class is an easy way to remember to do that, and minimizes the copies of a
 * method that isn't practical to test.
 */
public abstract class WritableResource extends SmpResource
{
    /**
     * Key used for all validation error datasets returned from writeable resources.
     */
    protected static final String VALIDATION_ERRORS_KEY = "validationErrors";
    
    /**
     * Transactional doesn't get obeyed unless it's here.
     */
    @Override
    @Transactional
    public void handleGet()
    {
        super.handleGet();
    }
    
    /**
     * Transactional doesn't get obeyed unless it's here.
     */
    @Override
    @Transactional
    public void handlePut()
    {
        super.handlePut();
    }

    /**
     * Transactional doesn't get obeyed unless it's here.
     */
    @Override
    @Transactional
    public void handlePost()
    {
        super.handlePost();
    }

    /**
     * Transactional doesn't get obeyed unless it's here.
     */
    @Override
    @Transactional
    public void handleDelete()
    {
        super.handleDelete();
    }
}
