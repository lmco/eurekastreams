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
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Where;
import org.hibernate.validator.Length;


/**
 * Tab template is a cookie cutter used for creating tabs with a given set
 * of starter information.
 *
 */
@SuppressWarnings("serial")
@Entity
public class TabTemplate extends DomainEntity implements Serializable
{

    /**
     * Max characters for tab name.
     */
    @Transient
    public static final int MAX_TAB_NAME_LENGTH = 16;

    /**
     * Max characters for tab name.
     */
    @Transient
    public static final String MAX_TAB_NAME_MESSAGE = "Tab name must be between 1 and "
                                                    + MAX_TAB_NAME_LENGTH + " characters.";

    /**
     * Store the value of the TabName.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_TAB_NAME_LENGTH, message = MAX_TAB_NAME_MESSAGE)
    private String tabName;

    /**
     * Enum value of the Layout definition the the tab. The EnumType.STRING was
     * explicit because it forces hibernate to store the value in the database
     * as a string. This is important because the default uses an integer value
     * and if more layouts are added later the integer value could get messed
     * up. The string tends to be more stable.
     */
    @Basic(fetch = FetchType.EAGER, optional = false)
    @Enumerated(EnumType.STRING)
    private Layout tabLayout;

    /**
     * this is a temporary field, tabs don't inherently have a tab type .
     */
    @Basic
    @Enumerated(EnumType.STRING)
    private TabType type;


    /**
     * The gadgets that are contained in this TabTemplate. Lazy load this collection
     * because the Tab Groups will want to show all of the Tabs while only
     * loading the current Tab's Gadgets.
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "tabTemplateId")
    @OrderBy("zoneNumber, zoneIndex")
    @Where(clause = "deleted='false'")
    @Cascade({ org.hibernate.annotations.CascadeType.ALL,
                org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<Gadget> gadgets;

    /**
     * The deleted field is used to track the state of the tab template object.
     */
    @SuppressWarnings("unused")
    @Basic(optional = false)
    private boolean deleted;

    /**
     * This is a timestamp that is used to track when a tab template was deleted. Since
     * the deleted record remains in the db until it expires (logic for cleanup
     * is defined in the mapper), this value needs to track the full date and
     * time of when the tab was deleted so that it can be cleaned up with a
     * minute based expiration.
     */
    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDeleted;

    /**
     * Protected constructor for ORM.
     */
    protected TabTemplate()
    {
    }

    /**
     * Constructor to create a TabTemplate base on existing TabTemplate.
     * @param inTabTemplate TabTemplate to use as "template".
     */
    public TabTemplate(final TabTemplate inTabTemplate)
    {
        tabLayout = inTabTemplate.getTabLayout();
        tabName = inTabTemplate.getTabName();

        List<Gadget> resultGadgets = new ArrayList<Gadget>();
        for (Gadget g : inTabTemplate.getGadgets())
        {
            resultGadgets.add(new Gadget(g));
        }
        this.setGadgets(resultGadgets);
    }

    /**
     * Public constructor for API.
     *
     * @param inTabName
     *            The name to display in the tab
     * @param inTabLayout
     *            The Layout for the tab.
     */
    public TabTemplate(final String inTabName, final Layout inTabLayout)
    {
        this.tabName = inTabName;
        this.tabLayout = inTabLayout;
        this.gadgets = new ArrayList<Gadget>();
    }

    /**
     * Get the name of the tab.
     *
     * @return the name of the tab
     */
    public String getTabName()
    {
        return tabName;
    }

    /**
     * This method is a required settings for the tab name to satisfy
     * serialization requirements in GWT.
     *
     * @param inTabName
     *            The name of the tab.
     */
    public void setTabName(final String inTabName)
    {
        tabName = inTabName;
    }

    /**
     * This method retrieves the current layout for this tab.
     *
     * @return current tab layout.
     */
    public Layout getTabLayout()
    {
        return tabLayout;
    }

    /**
     * This Method is a required setting for the layout to satisfy serialization
     * requirements in GWT.
     *
     * @param inTabLayout
     *            The layout of the tab.
     */
    public void setTabLayout(final Layout inTabLayout)
    {
        tabLayout = inTabLayout;
    }

    /**
     * @param inGadgets the gadgets to set
     */
    public void setGadgets(final List<Gadget> inGadgets)
    {
        gadgets = inGadgets;
    }

    /**
     * @return the gadgets
     */
    public List<Gadget> getGadgets()
    {
        return gadgets;
    }

    /**
     * @return the type
     */
    public TabType getType()
    {
        return type;
    }

    /**
     * @param inType the type to set
     */
    @SuppressWarnings("unused")
    private void setType(final TabType inType)
    {
        type = inType;
    }



}
