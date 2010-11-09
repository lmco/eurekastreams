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

/**
 * Utilities for cleaning input.
 */
public final class InputCleaner
{
    /** Disallow instantiation. */
    private InputCleaner()
    {
    }

    /**
     * Strips HTML tags from the input string.
     *
     * @param input
     *            the input string.
     * @param maxLength
     *            Maximum length of string to return.
     * @return the output string.
     */
    public static String stripHtml(final String input, final int maxLength)
    {
        String out = input.replaceAll("\\<.*?\\>", "");
        if (out.length() > maxLength)
        {
            return out.substring(0, maxLength);
        }

        return out;
    }
}
