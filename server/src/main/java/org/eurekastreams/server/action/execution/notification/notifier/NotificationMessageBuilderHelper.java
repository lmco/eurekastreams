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
package org.eurekastreams.server.action.execution.notification.notifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.velocity.context.Context;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Class which provides helper routines for building notification messages.
 */
public class NotificationMessageBuilderHelper
{
    /** Eureka variable start marker. */
    private static final String VARIABLE_START_MARKER = "%EUREKA:";

    /** Eureka variable end marker. */
    private static final String VARIABLE_END_MARKER = "%";

    /** Regex for extracting markdown links. */
    private static final String MARKDOWN_LINK_REGEX = "\\[([^\\]\\[]+)\\]\\(([^()]+)\\)";
    // TODO: Need better markdown conversion

    /** Compiled regex for extracting markdown links. */
    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern
            .compile(MARKDOWN_LINK_REGEX, Pattern.CASE_INSENSITIVE);

    /** Base URL to prepend to relative URLs. */
    private final String baseUrl;

    /**
     * Constructor.
     *
     * @param inBaseUrl
     *            Base URL to prepend to relative URLs.
     */
    public NotificationMessageBuilderHelper(final String inBaseUrl)
    {
        baseUrl = inBaseUrl;
    }

    /**
     * Returns a variable-substituted version of the activity's body.
     *
     * @param activity
     *            Activity.
     * @param context
     *            Velocity context.
     * @return Activity body text.
     */
    public String resolveActivityBody(final ActivityDTO activity, final Context context)
    {
        StrSubstitutor transform = new StrSubstitutor(new StrLookup()
        {
            @Override
            public String lookup(final String variableName)
            {
                if ("ACTORNAME".equals(variableName))
                {
                    return activity.getActor().getDisplayName();
                }
                else
                {
                    return null;
                }
            }
        }, VARIABLE_START_MARKER, VARIABLE_END_MARKER, StrSubstitutor.DEFAULT_ESCAPE);
        String result = transform.replace(activity.getBaseObjectProperties().get("content"));
        return result;
    }

    /**
     * Resolves the subset of markdown supported by Eureka into appropriate text.
     *
     * @param input
     *            String potentially with markdown.
     * @return String with markdown replaced.
     */
    public String resolveMarkdownForText(final String input)
    {
        Matcher matcher = MARKDOWN_LINK_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            matcher.appendReplacement(sb, ""); // use empty string here since it treats $ and \ specially
            String text = matcher.group(1);
            String url = matcher.group(2);
            if (url.charAt(0) == '#')
            {
                url = baseUrl + url;
            }
            sb.append(text).append(" (").append(url).append(")");
        }
        if (sb.length() > 0)
        {
            matcher.appendTail(sb);
            return sb.toString();
        }
        else
        {
            return input;
        }
    }

    /**
     * Resolves the subset of markdown supported by Eureka into appropriate text and HTML-escapes all input.
     *
     * @param input
     *            String potentially with markdown.
     * @return String with markdown replaced.
     */
    public String resolveMarkdownForHtml(final String input)
    {
        Matcher matcher = MARKDOWN_LINK_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        int copyFrom = 0;
        while (matcher.find())
        {
            sb.append(StringEscapeUtils.escapeHtml(input.substring(copyFrom, matcher.start())));
            copyFrom = matcher.end();

            String text = matcher.group(1);
            String url = matcher.group(2);
            if (url.charAt(0) == '#')
            {
                url = baseUrl + url;
            }
            sb.append("<a href=\"").append(StringEscapeUtils.escapeHtml(url)).append("\">")
                    .append(StringEscapeUtils.escapeHtml(text)).append("</a>");
        }
        if (copyFrom > 0)
        {
            sb.append(StringEscapeUtils.escapeHtml(input.substring(copyFrom, input.length())));
            return sb.toString();
        }
        else
        {
            return StringEscapeUtils.escapeHtml(input);
        }
    }
}
