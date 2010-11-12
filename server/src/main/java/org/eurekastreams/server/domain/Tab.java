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
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Min;

/**
 * Represents a web page tab The @Where annotation is used by Hibernate to conditionally load this entity. Since we are
 * now using the deleted field to act as a flag for deleted objects we need to ensure that the deleted records are not
 * being returned when asking for active tabs.
 */
@SuppressWarnings("serial")
@Entity
public class Tab extends DomainEntity implements Serializable
{

    /**
     * Max characters for tab name.
     */
    @Transient
    public static final String TAB_INDEX_MESSAGE = "Index must be >= zero";

    /**
     * Private reference back to the parent TabGroup for mapper queries originating with the Tab.
     */
    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabGroupId")
    private TabGroup tabGroup;

    /**
     * tabIndex field used by Hibernate to persist the order of Tabs in a collection. Set nullable=true for the
     * transient state when tabs are being moved around. Hibernate likes to unmap it, then remap it.
     */
    @Column(nullable = true)
    @Min(value = 0, message = TAB_INDEX_MESSAGE)
    private int tabIndex;

    /**
     * The deleted field is used to track the state of the tab object.
     */
    @SuppressWarnings("unused")
    @Basic(optional = false)
    private boolean deleted;

    /**
     * This is a timestamp that is used to track when a tab was deleted. Since the deleted record remains in the db
     * until it expires (logic for cleanup is defined in the mapper), this value needs to track the full date and time
     * of when the tab was deleted so that it can be cleaned up with a minute based expiration.
     */
    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDeleted;

    // TODO is this the right cascade type?
    /**
     *
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "templateId")
    @Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private TabTemplate template;

    /**
     * Protected constructor for ORM.
     */
    protected Tab()
    {
        template = new TabTemplate();
    }

    /**
     * public constructor.
     *
     * @param inTemplate
     *            Template for tab.
     */
    public Tab(final TabTemplate inTemplate)
    {
        this.template = inTemplate;
    }

    /**
     * Public constructor for API.
     *
     * @param inTabName
     *            The name to display in the tab
     * @param inTabLayout
     *            The Layout for the tab.
     */
    public Tab(final String inTabName, final Layout inTabLayout)
    {
        template = new TabTemplate(inTabName, inTabLayout);
    }

    /**
     * Public constructor for API.
     *
     * @param inTabName
     *            The name to display in the tab
     * @param inTabLayout
     *            The Layout for the tab.
     * @param inId
     *            set the id of the new tab.
     */
    public Tab(final String inTabName, final Layout inTabLayout, final Long inId)
    {
        this(inTabName, inTabLayout);
        this.setId(inId);
    }

    /**
     * Get the index of the tab.
     *
     * @return the index of the tab
     */
    public int getTabIndex()
    {
        return tabIndex;
    }

    /**
     * Set the index of the tab - intended for serialization only, which is why this is private.
     *
     * @param inTabIndex
     *            the new value to set the tabIndex.
     */
    public void setTabIndex(final int inTabIndex)
    {
        this.tabIndex = inTabIndex;
    }

    /**
     * Get the name of the tab.
     *
     * @return the name of the tab
     */
    public String getTabName()
    {
        return template.getTabName();
    }

    /**
     * This method is a required settings for the tab name to satisfy serialization requirements in GWT.
     *
     * @param inTabName
     *            The name of the tab.
     */
    public void setTabName(final String inTabName)
    {
        template.setTabName(inTabName);
    }

    /**
     * This method retrieves the current layout for this tab.
     *
     * @return current tab layout.
     */
    public Layout getTabLayout()
    {
        return template.getTabLayout();
    }

    /**
     * This Method is a required setting for the layout to satisfy serialization requirements in GWT.
     *
     * @param inTabLayout
     *            The layout of the tab.
     */
    public void setTabLayout(final Layout inTabLayout)
    {
        template.setTabLayout(inTabLayout);
    }

    /**
     * Get the Gadgets that are contained within this Tab.
     *
     * @return the Gadgets that are contained within this Tab.
     */
    public List<Gadget> getGadgets()
    {
        return template.getGadgets();
    }

    /**
     * Private setter for serialization purposes.
     *
     * @param inGadgets
     *            The list of gadgets.
     */
    @SuppressWarnings("unused")
    public void setGadgets(final List<Gadget> inGadgets)
    {
        template.setGadgets(inGadgets);
    }

    /**
     * @return the template
     */
    public TabTemplate getTemplate()
    {
        return template;
    }

    /**
     * @param inTemplate
     *            the template to set
     */
    @SuppressWarnings("unused")
    private void setTemplate(final TabTemplate inTemplate)
    {
        template = inTemplate;
    }
}
