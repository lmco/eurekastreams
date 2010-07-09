/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * This class is not intended to be called outside of the
 * domain model.  It is used for data storage only.
 *
 */
@SuppressWarnings("serial")
@Entity
public class AppDataValue extends DomainEntity implements Serializable
{

    /**
     * Key to identify the the data being stored.
     */
    @Basic
    private String name;

    /**
     * Value to be stored.
     */
    @Basic
    @Lob
    private String value;

    /**
     * Instance of AppData that this key/value pair is associated with.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appDataId")
    private AppData appData;

    /**
     * Default private constructor.
     */
    @SuppressWarnings("unused")
    private AppDataValue()
    {

    }

    /**
     * Main Constructor that creates the AppData Value.
     * @param inName - name or key for the value
     * @param inValue - value to be stored.
     * @param inAppData - appData instance this is being used with.
     */
    public AppDataValue(final String inName, final String inValue, final AppData inAppData)
    {
        setName(inName);
        setValue(inValue);
        setAppData(inAppData);
    }

    /**
     * @return the name/key associated with the data.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param inName set the name/key for the value to be stored.
     */
    private void setName(final String inName)
    {
        this.name = inName;
    }

    /**
     * @return the value of the data.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param inValue data to be stored.
     */
    public void setValue(final String inValue)
    {
        this.value = inValue;
    }

    /**
     * @return the instance of AppData that this name/value pair is associated with.
     */
    public AppData getAppData()
    {
        return appData;
    }

    /**
     * @param inAppData the instance of AppData that this name/value pair is associated with.
     */
    private void setAppData(final AppData inAppData)
    {
        this.appData = inAppData;
    }

}
