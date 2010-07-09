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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Represents a task that a gadget def has for a user to complete.
 *
 */
@SuppressWarnings("serial")
@Entity
public class Task extends DomainEntity implements Serializable
{
    /**
     * This is a list of the people who have completed this task. This is *ONLY*
     * used to clean up the table on delete.
     */
    @SuppressWarnings("unused")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Person_Task", joinColumns = { @JoinColumn(table = "Task", name = "taskId") },
            inverseJoinColumns = { @JoinColumn(table = "Person", name = "personId") })
    private List<Person> taskOwners;

    /**
     * This is a list of the orgs who have completed this task. This is *ONLY*
     * used to clean up the table on delete.
     */
    @SuppressWarnings("unused")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Organization_Task", joinColumns = { @JoinColumn(table = "Task", name = "taskId") },
            inverseJoinColumns = { @JoinColumn(table = "Organization", name = "organizationId") })
    private List<Organization> taskOrgs;

    /**
     * The gadget definition of the task.
     */
    @ManyToOne
    @JoinColumn(name = "gadgetDefinitionId")
    private GadgetDefinition gadgetDefinition;

    /**
     * The name of the task.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The description of the task.
     */
    @Column(nullable = false)
    private String description;

    /**
     * The name of the tab.
     */
    @Transient
    private String tab = "";

    /**
     * Default constructor.
     */
    protected Task()
    {

    }

    /**
     * Default constructor.
     *
     * @param inName
     *            the task name.
     * @param inDescription
     *            the task description.
     */
    public Task(final String inName, final String inDescription)
    {
        name = inName;
        description = inDescription;
    }

    /**
     * Constructor allowing you to define a tab.
     *
     * @param inName
     *            The task name.
     * @param inDescription
     *            The task description.
     * @param inTab
     *            The task tab.
     */
    public Task(final String inName, final String inDescription, final String inTab)
    {
        name = inName;
        description = inDescription;
        tab = inTab;
    }

    /**
     * Gets the gadget definition id.
     *
     * @return the gadget definition id.
     */
    public GadgetDefinition getGadgetDefinition()
    {
        return gadgetDefinition;
    }

    /**
     * Private setter for serialization.
     *
     * @param inGadgetDefinition
     *            the gadget def.
     */
    @SuppressWarnings("unused")
    private void setGadgetDefinition(final GadgetDefinition inGadgetDefinition)
    {
        gadgetDefinition = inGadgetDefinition;
    }

    /**
     * Gets the task name.
     *
     * @return the task name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the task name.
     *
     * @param inName
     *            the task name,
     */
    public void setName(final String inName)
    {
        name = inName;
    }

    /**
     * The task description.
     *
     * @return the task description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the task description.
     *
     * @param inDescription
     *            the task description.
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * Gets the tab.
     *
     * @return the tab name.
     */
    public String getTab()
    {
        return tab;
    }

    /**
     * Sets the tab.
     *
     * @param inTab
     *            the tab name.
     */
    public void setTab(final String inTab)
    {
        tab = inTab;
    }

    /**
     * Needed for checkstyle.
     *
     * @return the hashcode.
     */
    @Override
    public int hashCode()
    {
        return this.getName().hashCode();
    }

    /**
     * Overrides equal so that we can compare tasks based on their name.
     *
     * @param obj
     *            the input task.
     * @return result the boolean.
     */
    @Override
    public boolean equals(final Object obj)
    {
        return ((Task) obj).getName().equals(this.getName());
    }
}
