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
package org.eurekastreams.server.service.actions.strategies.activity;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.service.actions.strategies.MapParameterValidatorDecorator;

/**
 * This class provides the object validation for a Bookmark Activity.
 *
 */
public class BookmarkObjectValidator implements ActivityValidator
{
    /**
     * Local instance of the MapParameterValidatorDecorator for Bookmark Objects.
     */
    private MapParameterValidatorDecorator mapValidator;
    
    /**
     * Constructor for Bookmark object validator.
     * @param inMapValidator - Map Validator decorator to use for validation.
     */
    public BookmarkObjectValidator(final MapParameterValidatorDecorator inMapValidator)
    {
        mapValidator = inMapValidator;
    }

    /**
     * {@inheritDoc}
     */
    public void validate(final ActivityDTO inActivity)
    {
        HashMap<String, Serializable> mapValues = 
            new HashMap<String, Serializable>(inActivity.getBaseObjectProperties());
        mapValidator.validate(mapValues, new HashMap<String, String>());
    }

}
