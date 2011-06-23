/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action;

import java.util.ArrayList;
import java.util.Collection;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.junit.Assert;

/**
 * Helpers for testing classes in the action framework.
 */
public final class ActionTestHelper
{
    /** Forbid instantiation. */
    private ActionTestHelper()
    {
    }

    /**
     * Verifies that the async action request list contains the actions specified.
     *
     * @param actualRequests
     *            List of requests.
     * @param expectedActionKeys
     *            Names of the expected actions.
     */
    public static void assertAsyncActionRequests(final Collection<UserActionRequest> actualRequests,
            final String... expectedActionKeys)
    {
        ArrayList<String> actualKeys = new ArrayList<String>();
        for (UserActionRequest r : actualRequests)
        {
            actualKeys.add(r.getActionKey());
        }
        for (String s : expectedActionKeys)
        {
            if (!actualKeys.remove(s))
            {
                Assert.fail("Missing expected action request '" + s + "'");
            }
        }
        if (!actualKeys.isEmpty())
        {
            Assert.fail("Unexpected action requests found:  " + actualKeys);
        }
    }

    /**
     * Verifies that the async action request list contains the actions specified.
     *
     * @param context
     *            Context containing the list of requests.
     * @param expectedActionKeys
     *            Names of the expected actions.
     */
    public static void assertAsyncActionRequests(final TaskHandlerActionContext context,
            final String... expectedActionKeys)
    {
        assertAsyncActionRequests(context.getUserActionRequests(), expectedActionKeys);
    }
}
