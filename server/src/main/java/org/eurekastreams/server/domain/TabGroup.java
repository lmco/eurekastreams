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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Where;

/**
 * Represents a web page that contains a list of tabs.
 */
@SuppressWarnings("serial")
@Entity
public class TabGroup extends DomainEntity implements Serializable
{
    /**
     * Controls whether this page is read-only or should be writable. Normally is writable.
     */
    @Transient
    private boolean readOnly = false;

    /**
     * The list of tabs in this tab Group.
     *
     * Note: @IndexColumn is a Hibernate-specific annotation. The tabIndex field in the Tab object is never referenced
     * by this API, only Hibernate.
     */
    @IndexColumn(name = "tabIndex", base = 0)
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "tabGroupId")
    @Where(clause = "deleted='false'")
    private List<Tab> tabs = new ArrayList<Tab>();

    /**
     * Get the ordered collection of tabs - any change in order is persisted.
     *
     * @return the collection of tabs
     */
    public List<Tab> getTabs()
    {
        return tabs;
    }

    /**
     * Needed for serialization.
     *
     * @param inTabs
     *            the tabs.
     */
    public void setTabs(final List<Tab> inTabs)
    {
        tabs = inTabs;
    }

    /**
     * Add a tab to the current list.
     *
     * @param tab
     *            The tab to add.
     */
    public void addTab(final Tab tab)
    {
        tabs.add(tab);
    }

    /**
     * Getter.
     *
     * @return the readOnly
     */
    public boolean getReadOnly()
    {
        return readOnly;
    }

    /**
     * Setter.
     *
     * @param inReadOnly
     *            the readOnly to set
     */
    public void setReadOnly(final boolean inReadOnly)
    {
        readOnly = inReadOnly;
    }

    /**
     * Public constructor for ORM and ResourcePersistenceStrategy.
     */
    public TabGroup()
    {
    }
}
