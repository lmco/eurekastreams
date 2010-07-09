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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Representation of an activity/message stream, containing a scope and (optionally) keywords.
 */
@Entity
public class StreamView extends DomainEntity implements Serializable, StreamFilter
{
    /**
     * This is used for the default views everyone gets.
     *
     */
    public enum Type
    {
        /**
         * Not set.
         */
        NOTSET,
        /**
         * Everyone view. A view with 1 scope with type EVERYONE.
         */
        EVERYONE,
        /**
         * Parent Org view. A view with 1 scope with type PARENT_ORG
         */
        PARENTORG,
        /**
         * People Im following.
         */
        PEOPLEFOLLOW,
        
        /**
         * Starred activities.
         */
        STARRED
    }
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -9154582842531623839L;
    
    /**
     * Tag used in data store to represent name of StreamView of person's parent org.
     * This is used so action that creates the StreamView can give it this known 
     * system tag as name, then action that returns views can search for this tag
     * and replace it with actual name of person's parent org.
     */
    public static final String PARENT_ORG_TAG = "EUREKA:PARENT_ORG_TAG";

    /**
     * List of scopes to include in the stream (OR'ed).
     */
    @ManyToMany(fetch = FetchType.EAGER)    
    private Set<StreamScope> includedScopes = new HashSet<StreamScope>();    
    
    /**
     * The name of the view.
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * Stores the view.
     */
    @Enumerated(EnumType.STRING)
    private StreamView.Type type;
    
    
    /**
     * Sets the name of the view.
     * @param inName the name.
     */
    public void setName(final String inName)
    {
        this.name = inName;
    }
    
    /**
     * Gets the name.
     * @return the name.
     */
    public String getName()
    {
        return this.name;
    }    

    /**
     * Get the included scopes.
     * 
     * @return the includedScopes
     */
    public Set<StreamScope> getIncludedScopes()
    {
        return includedScopes;
    }

    /**
     * Set the included scopes.
     * 
     * @param inIncludedScopes
     *            the includedScopes to set
     */
    public void setIncludedScopes(final Set<StreamScope> inIncludedScopes)
    {
        this.includedScopes = inIncludedScopes;
    }
    
    /**
     * Retrieve the name of the DomainEntity. This is to allow for the super
     * class to identify the table within hibernate.
     * 
     * @return The name of the domain entity.
     */
    public static String getDomainEntityName()
    {
        return "StreamView";
    }

    /**
     * @return the type
     */
    public StreamView.Type getType()
    {
        return type;
    }

    /**
     * @param inType the type to set
     */
    public void setType(final StreamView.Type inType)
    {
        this.type = inType;
    }
}
