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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * This class provides the model for supporting OpenSocial Application Data.
 *
 */
@SuppressWarnings("serial")
@Entity
public class AppData extends DomainEntity implements Serializable
{

    /**
     * List of the AppData values that are associated with the Gadget and
     * Person.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "appDataId")
    private List<AppDataValue> appDataValues = new ArrayList<AppDataValue>();

    /**
     * This is many to one because there is no sense in using a gadget to get
     * all of the application data because it could contain 1000's of entries.
     * This is takeaway from the Shindig db implementation example.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gadgetDefinitionId")
    private GadgetDefinition gadgetDefinition;

    /**
     * This is the reference to the person that is associated with this app
     * data.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person person;

    /**
     * Local map that will be exposed to the client. Used for better
     * manipulation of data and what the interfaces require.
     */
    @Transient
    private Map<String, String> values = new HashMap<String, String>();

    /**
     * Default constructor.
     */
    public AppData()
    {
        // Empty Constructor.
    }

    /**
     * Constructor that inputs Person and GadgetDefinition Objects.
     *
     * @param inPerson
     *            - instance of person to associate app data with.
     * @param inGadgetDefinition
     *            - instance of gadgetdefinition to associate app data with.
     */
    public AppData(final Person inPerson, final GadgetDefinition inGadgetDefinition)
    {
        gadgetDefinition = inGadgetDefinition;
        person = inPerson;
    }

    /**
     * Getter for the List of AppDataValues.
     *
     * @return List of App Data Values associated with this instance.
     */
    @SuppressWarnings("unused")
    private List<AppDataValue> getAppDataValues()
    {
        return appDataValues;
    }

    /**
     * Setter for the List of AppDataValues.
     *
     * @param inValues
     *            input List.
     */
    @SuppressWarnings("unused")
    private void setAppDataValues(final List<AppDataValue> inValues)
    {
        appDataValues = inValues;
    }

    /**
     * Retrieve the gadget instance associated with this AppData instance.
     *
     * @return gadget that this AppData is associated with.
     */
    public final GadgetDefinition getGadgetDefinition()
    {
        return gadgetDefinition;
    }

    /**
     * Set the gadget instance associated with this AppData instance.
     *
     * @param inGadgetDefinition
     *            the Gadget that is associated with this AppData.
     */
    public void setGadgetDefinition(final GadgetDefinition inGadgetDefinition)
    {
        this.gadgetDefinition = inGadgetDefinition;
    }

    /**
     * Get the person associated wit this AppData instance.
     *
     * @return person that this AppData is associated with.
     */
    public Person getPerson()
    {
        return this.person;
    }

    /**
     * Set the person associated with this AppData instance.
     *
     * @param inPerson
     *            the Person that is associated with this AppData.
     */
    public void setPerson(final Person inPerson)
    {
        this.person = inPerson;
    }

    /**
     * Get the Map of Values for this AppData instance.
     *
     * @return Map of key and value for this AppData instance.
     */
    public Map<String, String> getValues()
    {
        return Collections.unmodifiableMap(this.values);
    }

    /**
     * Set the Map of Values for this AppData instance.
     *
     * @param inValues
     *            Map of values.
     */
    public void setValues(final Map<String, String> inValues)
    {
        this.values = inValues;
        prePersist();
    }

    /**
     * This method is performed before the data is persisted to the database.
     * The concepts and logic were taken from samples in the Shindig project.
     * The idea behind this method is to convert the Map String, String exposed
     * to the caller back into the persisted structure of List AppDataValue.
     */
    @PrePersist
    public void prePersist()
    {
        // check for new items (in values but not in appDataValues)
        for (Entry<String, String> currentEntry : values.entrySet())
        {
            AppDataValue currentValue = findValueByKey(appDataValues, currentEntry.getKey());
            // If we have a new entry
            if (currentValue == null)
            {
                currentValue = new AppDataValue(currentEntry.getKey(), currentEntry.getValue(), this);
                appDataValues.add(currentValue);
            }
            else
            {
                currentValue.setValue(currentEntry.getValue());
            }
        }

        // check for ones to be deleted (in appDataValues but not in values)
        List<String> toRemove = new ArrayList<String>();
        for (AppDataValue currentAppDataEntry : appDataValues)
        {
            // If the current map of keys and values doesn't contain the current
            // entry's key, then it has been removed and don't persist it.
            if (!values.containsKey(currentAppDataEntry.getName()))
            {
                toRemove.add(currentAppDataEntry.getName());
            }
        }

        // Remove those keys identified to be deleted.
        for (String keyToRemove : toRemove)
        {
            appDataValues.remove(findValueByKey(appDataValues, keyToRemove));
        }
    }

    /**
     * This method occurs directly following the load of the data from the db.
     * This converts the List AppDataValues into a Map String, String that is
     * easier for a caller to consume.
     */
    @PostLoad
    public void postLoad()
    {
        for (AppDataValue currentEntry : appDataValues)
        {
            values.put(currentEntry.getName(), currentEntry.getValue());
        }
    }

    /**
     * Helper method to find data in the persisted List of AppDataValues.
     *
     * @param loadedValues
     *            - database loaded AppDataValues
     * @param key
     *            - key to find in the list of AppDataValues.
     * @return null if not found and the AppDataValue object if found.
     */
    private AppDataValue findValueByKey(final List<AppDataValue> loadedValues, final String key)
    {
        if (loadedValues != null)
        {
            for (AppDataValue currentValue : loadedValues)
            {
                if (currentValue.getName() == key)
                {
                    return currentValue;
                }
            }
        }
        return null;
    }
}
