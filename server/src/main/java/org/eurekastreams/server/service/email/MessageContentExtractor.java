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
package org.eurekastreams.server.service.email;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;

/**
 * Extracts the user-provided content from an email. This implementation is somewhat simplistic. It uses the first
 * non-empty non-attachment plain text part found. It returns the text as a simple string (no support for attaching
 * links to activity posts). It removes any forwarded or replied-to content, and does so assuming that the user's entry
 * is above the prior message.
 */
public class MessageContentExtractor
{
    /**
     * Extracts the user-provided content from an email.
     *
     * @param message
     *            The email message.
     * @return The content text.
     * @throws MessagingException
     *             On error.
     * @throws IOException
     *             On error.
     */
    public String extract(final Message message) throws MessagingException, IOException
    {
        return findAndExtract(message);
    }

    /**
     * Recursive method to find content in a message part.
     *
     * @param part
     *            Part to check.
     * @return The content text if found in the part or a subpart, else null.
     * @throws MessagingException
     *             On error.
     * @throws IOException
     *             On error.
     */
    private String findAndExtract(final Part part) throws MessagingException, IOException
    {
        ContentType contentType = new ContentType(part.getContentType());

        // check if usable plain text content
        if (part.getDisposition() == null && "text/plain".equals(contentType.getBaseType()))
        {
            String text = extractUserText(part);
            if (text != null)
            {
                return text;
            }
        }

        // recurse if multipart content
        if ("multipart".equals(contentType.getPrimaryType()))
        {
            Object content = part.getContent();
            if (content instanceof Multipart)
            {
                Multipart mp = (Multipart) content;
                int count = mp.getCount();
                for (int i = 0; i < count; i++)
                {
                    BodyPart childPart = mp.getBodyPart(i);
                    String text = findAndExtract(childPart);
                    if (text != null)
                    {
                        return text;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Extracts the content text from the current part, removing any forwarded or replied-to message text and unwanted
     * whitespace.
     *
     * @param part
     *            Part containing content.
     * @return Content text.
     * @throws IOException
     *             On error.
     * @throws MessagingException
     *             On error.
     */
    private String extractUserText(final Part part) throws IOException, MessagingException
    {
        String content = (String) part.getContent();

        // look for the beginning of a forwarded or replied message
        int end = content.indexOf("\r\nFrom: ");
        if (end == -1)
        {
            end = content.length();
        }

        // remove trailing newlines and whitespace
        while (end > 0 && Character.isWhitespace(content.charAt(end - 1)))
        {
            end--;
        }
        // remove leading newlines and whitespace
        int start = 0;
        while (start < end && Character.isWhitespace(content.charAt(start)))
        {
            start++;
        }

        return start < end ? content.substring(start, end) : null;
    }
}
