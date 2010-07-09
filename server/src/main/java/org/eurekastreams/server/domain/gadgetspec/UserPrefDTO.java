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

import java.util.List;
import java.util.Map;

/**
 * Data class for mapping Gadget MetaData information.
 *
 */
public class UserPrefDTO
{
    /**
     * Name for the UserPrefDTO.
     */
    private String name;
    
    /**
     * Display name for the UserPrefDTO.
     */
    private String displayName;
    
    /**
     * Default value to be used in the input box.
     */
    private String defaultValue;
    
    /**
     * Flag to set whether or not the field is required.
     */
    private boolean required;

    /**
     * Map of enum values for quick access with the name being the key.
     */
    private Map<String, String> enumValues;
    
    /**
     * List of EnumValuePairDTO objects to make the generation of an ordered
     * select box easier.
     */
    private List<EnumValuePairDTO> orderedEnumValues;
    
    /**
     * User Preferences data type.
     */
    private DataType dataType;
        
    /**
     * Getter for the Name field.
     * @return name field.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Setter for the Name field.
     * @param inName input of the name field.
     */
    public void setName(final String inName)
    {
        name = inName;
    }
    
    /**
     * Getter for the Display Name.
     * @return display name of the current user preference.
     */
    public String getDisplayName()
    {
        return displayName;
    }
    
    /**
     * Setter for the Display Name.
     * @param inDisplayName input of the display name field.
     */
    public void setDisplayName(final String inDisplayName)
    {
        displayName = inDisplayName;
    }
    
    /**
     * Getter for the Default Value.
     * @return default value of the current user preference.
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }
    
    /**
     * Setter for the Default Value.
     * @param inDefaultValue input of the default value field.
     */
    public void setDefaultValue(final String inDefaultValue)
    {
        defaultValue = inDefaultValue;
    }
    
    /**
     * Getter for the Required field.
     * @return required value of the current user preference.
     */
    public boolean getRequired()
    {
        return required;
    }
    
    /**
     * Setter for the Required field.
     * @param inRequired input of the required field.
     */
    public void setRequired(final String inRequired)
    {
        required = Boolean.parseBoolean(inRequired);
    }
    
    /**
     * Getter for the Map of enum values.
     * @return Map of enum values.
     */
    public Map<String, String> getEnumValues()
    {
        return enumValues;
    }
    
    /**
     * Setter for the Map of enum values.
     * @param inEnumValues input of the enum values.
     */
    public void setEnumValues(final Map<String, String> inEnumValues)
    {
        enumValues = inEnumValues;
    }
    
    /**
     * Getter for the DataType of the User Preference.
     * @return datatype for the user preference.
     */
    public DataType getDataType()
    {
        return dataType;
    }
    
    /**
     * Setter for the DataType of the User Preference.
     * @param inDataType input for the DataType.
     */
    public void setDataType(final String inDataType)
    {
        dataType = DataType.parse(inDataType);
    }
    
    /**
     * Getter for the List of EnumValuePairs that is ordered.
     * @return List of ordered EnumValuePairs.
     */
    public List<EnumValuePairDTO> getOrderedEnumValues()
    {
        return orderedEnumValues;
    }
    
    /**
     * Setter for the List of EnumValuePairs that is ordered.
     * @param inOrderedEnumValues input for the List of EnumValuePairs.
     */
    public void setOrderedEnumValues(final List<EnumValuePairDTO> inOrderedEnumValues)
    {
        orderedEnumValues = inOrderedEnumValues;
    }

    /**
     * Enum of acceptable datatypes in the UserPreferences.
     *
     */
    public static enum DataType
    {
        /**
         * String DataType.
         */
        STRING, 
        /**
         * Hidden DataType.
         */
        HIDDEN, 
        /**
         * Boolean DataType.
         */
        BOOL, 
        /**
         * Enum DataType.
         */
        ENUM,
        /**
         * List DataType.
         */
        LIST, 
        /**
         * Number DataType.
         */
        NUMBER;

        /**
         * Parses a data type from the input string.
         *
         * @param value string value to conver to Enum.
         * @return The data type of the given value.
         */
        public static DataType parse(final String value) 
        {
          for (DataType type : DataType.values()) 
          {
            if (type.toString().compareToIgnoreCase(value) == 0) 
            {
              return type;
            }
          }
          return STRING;
        }
    }
}
