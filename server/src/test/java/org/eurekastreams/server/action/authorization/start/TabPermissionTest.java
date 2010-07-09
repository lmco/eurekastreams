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
package org.eurekastreams.server.action.authorization.start;

import junit.framework.Assert;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.db.GetTabPermissionByPersonAndTab;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test for TabPermission.
 *
 */
public class TabPermissionTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock.
     */
    private final GetTabPermissionByPersonAndTab tabPermissionMapper =
        context.mock(GetTabPermissionByPersonAndTab.class);

    /**
     * Mock.
     */
    private final UserDetails userDetails = context.mock(UserDetails.class);

    /**
     * Test user id for test suite.
     */
    private static final String TEST_USER_ACCOUNTID = "testaccount";

    /**
     * Mock.
     */
    private TabPermission sut;

    /**
     * Setup for each test.
     */
    @Before
    public void set()
    {
        sut = new TabPermission(tabPermissionMapper);
    }

    /**
     * Expect fail with AuthorizationException.
     */
    @Test(expected = AuthorizationException.class)
    public void testCanDeleteStartPageTabFailWithException()
    {
        final long tabIdToDelete = 9L;

        setTabGroupIdExpectations(tabIdToDelete, false);

        sut.canDeleteStartPageTab(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), true);
        context.assertIsSatisfied();
    }

    /**
     * Expect return false (not allowed to delete tab).
     */
    @Test
    public void testCanDeleteStartPageTabFailWithFalse()
    {
        final long tabIdToDelete = 9L;

        setTabGroupIdExpectations(tabIdToDelete, false);

        Assert.assertFalse(sut.canDeleteStartPageTab(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }

    /**
     * Expect return true (delete allowed).
     */
    @Test
    public void testCanDeleteStartPageTabAllowDelete()
    {
        final long tabIdToDelete = 9L;

        setTabGroupIdExpectations(tabIdToDelete, true);

        Assert.assertTrue(sut.canDeleteStartPageTab(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }

    /**
     * Expect fail with AuthorizationException.
     */
    @Test(expected = AuthorizationException.class)
    public void testCanChangeTabLayoutFailWithException()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(false));
            }
        });

        sut.canChangeTabLayout(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), true);
        context.assertIsSatisfied();
    }

    /**
     * Expect return false (not allowed to delete tab).
     */
    @Test
    public void testCanChangeTabLayoutFailWithFalse()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(false));
            }
        });

        Assert.assertFalse(sut.canChangeTabLayout(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }

    /**
     * Expect return true (delete allowed).
     */
    @Test
    public void testCanChangeTabLayoutAllowChange()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(true));
            }
        });

        Assert.assertTrue(sut.canChangeTabLayout(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }

    /**
     * Expect fail with AuthorizationException.
     */
    @Test(expected = AuthorizationException.class)
    public void testCanModifyGadgetsFailWithException()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(false));
            }
        });

        sut.canModifyGadgets(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), true);
        context.assertIsSatisfied();
    }

    /**
     * Expect return false (not allowed to delete tab).
     */
    @Test
    public void testCanModifyGadgetsFailWithFalse()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(false));
            }
        });

        Assert.assertFalse(sut.canModifyGadgets(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }

    /**
     * Expect return true (delete allowed).
     */
    @Test
    public void testCanModifyGadgetsAllowChange()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(true));
            }
        });

        Assert.assertTrue(sut.canModifyGadgets(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }


    /**
     * Expect fail with AuthorizationException.
     */
    @Test(expected = AuthorizationException.class)
    public void testCanRenameTabFailWithException()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(false));
            }
        });

        sut.canModifyGadgets(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), true);
        context.assertIsSatisfied();
    }

    /**
     * Expect return false (not allowed to delete tab).
     */
    @Test
    public void testCanRenameTabFailWithFalse()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(false));
            }
        });

        Assert.assertFalse(sut.canModifyGadgets(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }

    /**
     * Expect return true (delete allowed).
     */
    @Test
    public void testCanRenameTabAllowChange()
    {
        final long tabIdToDelete = 9L;

        context.checking(new Expectations()
        {
            {
                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(true));
            }
        });

        Assert.assertTrue(sut.canModifyGadgets(TEST_USER_ACCOUNTID, new Long(tabIdToDelete), false));
        context.assertIsSatisfied();
    }

    /**
     * Set common expectations.
     * @param tabIdToDelete The tabId to delete.
     * @param returnValue the value (true or false) you expect to be returned from the request to the mapper.
     */
    private void setTabGroupIdExpectations(final long tabIdToDelete, final boolean returnValue)
    {
        context.checking(new Expectations()
        {
            {
                allowing(userDetails).getUsername();
                will(returnValue(TEST_USER_ACCOUNTID));

                oneOf(tabPermissionMapper).execute(TEST_USER_ACCOUNTID, tabIdToDelete);
                will(returnValue(returnValue));
            }
        });
    }

}
