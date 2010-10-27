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
/**
 * 
 */
package org.eurekastreams.server.domain;


/**
 * What states something can be in. 
 */
public final class TabType 
{ 
    /**  */
    public static final String ORG_ABOUT = "ORG_ABOUT"; 
    /**  */
    public static final String PERSON_ABOUT = "PERSON_ABOUT";
    /** */
    public static final String GROUP_ABOUT = "GROUP_ABOUT";
    /**  */
    public static final String WELCOME = "WELCOME"; 
    /**  */
    public static final String APP = "APP";
    
    /**
     * Default constructor override.
     */
    private TabType()
    {
        //Hiding default constructor for a utility class.
    }
}
