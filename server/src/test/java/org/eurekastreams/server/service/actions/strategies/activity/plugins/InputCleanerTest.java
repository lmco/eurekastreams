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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests InputCleaner.
 */
public class InputCleanerTest
{
    /**
     * Tests stripping markup.
     */
    @Test
    public void teststripHtml()
    {
        final int length = 20;
        String input = "<b>Formatted <i>Title</i></b>";
        String output = InputCleaner.stripHtml(input, length);
        assertEquals("Formatted Title", output);
    }

    /**
     * Tests stripping markup.
     */
    @Test
    public void teststripHtmlTooLong()
    {
        final int length = 11;
        String input = "<b>Formatted <i>Title</i></b>";
        String output = InputCleaner.stripHtml(input, length);
        assertEquals("Formatted T", output);
    }
}
