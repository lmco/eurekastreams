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
package org.eurekastreams.server.domain.gadgetspec;

/**
 * This class is a simple DTO for representing Enum Value
 * and DisplayValue pairs.
 *
 */
public class EnumValuePairDTO
{
    /**
     * Local instance of the value.
     */
    private final String value;
    
    /**
     * Local instance of the displayValue.
     */
    private final String displayValue;
    
    /**
     * Default constructor for the EnumValuePairDTO.
     * @param inValue - 
     *          string representing the value of the enum selection.
     * @param inDisplayValue - 
     *          string representing the display of the enum selection.
     */
    public EnumValuePairDTO(final String inValue, final String inDisplayValue)
    {
        value = inValue;
        displayValue = inDisplayValue;
    }
    
    /**
     * Getter for the value of the enum.
     * @return string value for the enum.
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Getter for the displayValue of the enum.
     * @return string displayValue for the enum.
     */
    public String getDisplayValue()
    {
        return displayValue;
    }
}
