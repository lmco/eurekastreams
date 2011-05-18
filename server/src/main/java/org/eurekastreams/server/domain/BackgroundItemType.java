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

import java.util.HashMap;

/**
 * enum for background items.
 * 
 * in an enum, name() and valueOf() are bidirectional
 * 
 * For our purposes, constructor/toEnum and toString are bidirectional.
 */
public enum BackgroundItemType
{
    /** Uninitialized value. */
    NOT_SET("Not Initialized"),

    /** An interest of a person. */
    INTEREST("Interests and Hobbies"),

    /** A skill of a person. */
    SKILL("Skills and Specialties"),

    /** An affiliation of a person. */
    AFFILIATION("Affiliations"),

    /** An honor of a person. */
    HONOR("Honors and Awards"),

    /** A capability. */
    CAPABILITY("Capability"),

    /** An area of study of a person. */
    AREA_OF_STUDY("Area of Study"),

    /** An activity or society of a person. */
    ACTIVITY_OR_SOCIETY("Activity or Society"),

    /** Name of school. */
    SCHOOL_NAME("School name");

    /**
     * human-readable name (ie, "Business Development" as opposed to enum.BD).
     */
    private final String displayableName;

    /**
     * for string-enum conversion.
     */
    private static HashMap<String, BackgroundItemType> map = new HashMap<String, BackgroundItemType>();

    static
    {
        BackgroundItemType[] types = BackgroundItemType.values();
        for (BackgroundItemType type : types)
        {
            map.put(type.toString(), type);
        }

    }

    /**
     * constructor for this enum.
     * 
     * @param name
     *            a displayable name.
     */
    private BackgroundItemType(final String name)
    {
        displayableName = name;
    }

    /**
     * 
     * @param displayableName
     *            to convert.
     * @return the enum corresponding to that string.
     */
    public static BackgroundItemType toEnum(final String displayableName)
    {
        return map.get(displayableName);
    }

    /**
     * @return the displayable name that was passed into the constructor.
     */
    @Override
    public String toString()
    {
        return displayableName;
    }

}
