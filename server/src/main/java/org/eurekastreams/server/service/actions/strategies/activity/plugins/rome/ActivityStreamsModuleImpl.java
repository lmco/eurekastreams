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
package org.eurekastreams.server.service.actions.strategies.activity.plugins.rome;

import com.sun.syndication.feed.module.ModuleImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Represents a converted ActivityStreamsModule.
 * 
 */
@SuppressWarnings("serial")
public class ActivityStreamsModuleImpl extends ModuleImpl implements ActivityStreamsModule
{
    /**
     * The atom entry.
     */
    private SyndEntryImpl atomEntry;
    /**
     * The object type.
     */
    private String objectType;

    /**
     * Default constructor.
     */
    public ActivityStreamsModuleImpl()
    {
        super(ActivityStreamsModule.class, ActivityStreamsModule.URI);
    }

    /**
     * copyFrom needed for ROME moduleness.
     * 
     * @param obj
     *            object to copy from.
     */
    @Override
    public void copyFrom(final Object obj)
    {
        ActivityStreamsModule module = (ActivityStreamsModule) obj;
        setAtomEntry(module.getAtomEntry());
        setObjectType(module.getObjectType());
    }

    /**
     * Get the interface, again, needed for ROME.
     * 
     * @return the interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class getInterface()
    {
        return ActivityStreamsModule.class;
    }

    /**
     * Gets the atom entry.
     * 
     * @return the atom entry.
     */
    @Override
    public SyndEntryImpl getAtomEntry()
    {
        return atomEntry;
    }

    /**
     * Gets the object type.
     * 
     * @return the object type.
     */
    @Override
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * Sets the atom entry.
     * 
     * @param inAtomEntry
     *            the atom entry.
     */
    @Override
    public void setAtomEntry(final SyndEntryImpl inAtomEntry)
    {
        atomEntry = inAtomEntry;
    }

    /**
     * Sets the object type.
     * 
     * @param inType
     *            the type.
     */
    @Override
    public void setObjectType(final String inType)
    {
        objectType = inType;
    }

}
