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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.Layout;

/**
 * Tab dto.
 * 
 */
public class TabDTO implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -303075213317986828L;

    /**
     * Tab id.
     */
    private long id;

    /**
     * tab gadgets.
     */
    private List<GadgetDTO> gadgets;

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }

    /**
     * @return the gadgets
     */
    public List<GadgetDTO> getGadgets()
    {
        return gadgets;
    }

    /**
     * @param inGadgets
     *            the gadgets to set
     */
    public void setGadgets(final List<GadgetDTO> inGadgets)
    {
        gadgets = inGadgets;
    }

    /**
     * @return the tabIndex
     */
    public int getTabIndex()
    {
        return tabIndex;
    }

    /**
     * @param inTabIndex
     *            the tabIndex to set
     */
    public void setTabIndex(final int inTabIndex)
    {
        tabIndex = inTabIndex;
    }

    /**
     * @return the tabLayout
     */
    public Layout getTabLayout()
    {
        return tabLayout;
    }

    /**
     * @param inTabLayout
     *            the tabLayout to set
     */
    public void setTabLayout(final Layout inTabLayout)
    {
        tabLayout = inTabLayout;
    }

    /**
     * @return the tabName
     */
    public String getTabName()
    {
        return tabName;
    }

    /**
     * @param inTabName
     *            the tabName to set
     */
    public void setTabName(final String inTabName)
    {
        tabName = inTabName;
    }

    /**
     * tabIndex field used by Hibernate to persist the order of Tabs in a collection. Set nullable=true for the
     * transient state when tabs are being moved around. Hibernate likes to unmap it, then remap it.
     */
    private int tabIndex;

    /**
     * Tab layout.
     */
    private Layout tabLayout;

    /**
     * Tab name.
     */
    private String tabName;

}
