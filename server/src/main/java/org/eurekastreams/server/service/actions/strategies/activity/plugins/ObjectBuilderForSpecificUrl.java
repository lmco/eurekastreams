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

import java.util.regex.Pattern;

/**
 * Wrapper to hold onto an object mapper and a regex for specific URL mapping ala Flickr, Youtube, etc.
 */
public class ObjectBuilderForSpecificUrl
{
    /** The regex to match the URL against. */
    private final Pattern regex;

    /** The object mapper to run when a match is found. */
    private final FeedObjectActivityBuilder builder;

    /**
     * Constructor.
     *
     * @param inRegex
     *            URL regex.
     * @param inBuilder
     *            Activity object builder.
     */
    public ObjectBuilderForSpecificUrl(final String inRegex, final FeedObjectActivityBuilder inBuilder)
    {
        regex = Pattern.compile(inRegex);
        builder = inBuilder;
    }

    /**
     * Determines if the URL applies to the builder.
     * 
     * @param url
     *            URL of feed.
     * @return If URL applies.
     */
    public boolean match(final String url)
    {
        return regex.matcher(url).find();
    }

    /**
     * Gets the activity object builder.
     *
     * @return Activity object builder.
     */
    public FeedObjectActivityBuilder getBuilder()
    {
        return builder;
    }
}
