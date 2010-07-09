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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.exceptions.GadgetDeletionException;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.UpdateGadgetsWithNewTabLayoutRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test suite for the {@link UpdateGadgetsWithNewTabLayoutMapper}.
 *
 */
public class UpdateGadgetsWithNewTabLayoutMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private UpdateGadgetsWithNewTabLayoutMapper sut;

    /**
     * Test tab template id.
     */
    private static final Long TEST_TABTEMPLATE_ID = 3253L;

    /**
     * This field holds the test instance of jpaTabMapper.
     */
    @Autowired
    private TabMapper jpaTabMapper;

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new UpdateGadgetsWithNewTabLayoutMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * TEst execution of the mapper.
     */
    @Test
    public void testExecute()
    {
        Tab fordsThirdTab = jpaTabMapper.findById(TEST_TABTEMPLATE_ID);

        // Ensure the state is good ahead of time.
        assertEquals(Layout.THREECOLUMN, fordsThirdTab.getTabLayout());
        assertEquals(7, fordsThirdTab.getGadgets().size());
        assertEquals(0, fordsThirdTab.getGadgets().get(0).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(1).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(2).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(3).getZoneNumber());
        assertEquals(2, fordsThirdTab.getGadgets().get(4).getZoneNumber());
        assertEquals(2, fordsThirdTab.getGadgets().get(5).getZoneNumber());
        assertEquals(2, fordsThirdTab.getGadgets().get(6).getZoneNumber());

        UpdateGadgetsWithNewTabLayoutRequest request = new UpdateGadgetsWithNewTabLayoutRequest(fordsThirdTab
                .getTemplate().getId(), Layout.TWOCOLUMN);
        sut.execute(request);

        getEntityManager().clear();

        fordsThirdTab = jpaTabMapper.findById(TEST_TABTEMPLATE_ID);

        assertEquals(7, fordsThirdTab.getGadgets().size());
        assertEquals(0, fordsThirdTab.getGadgets().get(0).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(1).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(2).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(3).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(4).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(5).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(6).getZoneNumber());
    }

    /**
     * TEst execution of the mapper.
     * @throws GadgetDeletionException - on error for deletion.
     */
    @Test
    public void testExecuteWithDeletedGadgetsAndEmptyTargetColumn() throws GadgetDeletionException
    {
        Tab fordsThirdTab = jpaTabMapper.findById(TEST_TABTEMPLATE_ID);

        // Ensure the state is good ahead of time.
        assertEquals(Layout.THREECOLUMN, fordsThirdTab.getTabLayout());
        assertEquals(7, fordsThirdTab.getGadgets().size());
        assertEquals(0, fordsThirdTab.getGadgets().get(0).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(1).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(2).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(3).getZoneNumber());
        assertEquals(2, fordsThirdTab.getGadgets().get(4).getZoneNumber());
        assertEquals(2, fordsThirdTab.getGadgets().get(5).getZoneNumber());
        assertEquals(2, fordsThirdTab.getGadgets().get(6).getZoneNumber());

        //Remove the last gadget in the middle column so that we simulate collapsing all gadgets
        //down to an empty column.
        jpaTabMapper.deleteGadget(fordsThirdTab.getGadgets().get(3));

        getEntityManager().clear();

        UpdateGadgetsWithNewTabLayoutRequest request = new UpdateGadgetsWithNewTabLayoutRequest(fordsThirdTab
                .getTemplate().getId(), Layout.TWOCOLUMN);
        sut.execute(request);

        getEntityManager().clear();

        fordsThirdTab = jpaTabMapper.findById(TEST_TABTEMPLATE_ID);

        assertEquals(6, fordsThirdTab.getGadgets().size());
        assertEquals(0, fordsThirdTab.getGadgets().get(0).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(1).getZoneNumber());
        assertEquals(0, fordsThirdTab.getGadgets().get(2).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(3).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(4).getZoneNumber());
        assertEquals(1, fordsThirdTab.getGadgets().get(5).getZoneNumber());
    }
}
