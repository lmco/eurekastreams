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
package org.eurekastreams.server.domain.strategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extract hashtags from a body of text, recognizing when the hash is part of a url, or parentheses.
 */
public class HashTagExtractor
{
    /**
     * The valid characters in a hashtag.
     */
    private static final String VALID_HASHTAG_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz#"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";

    /**
     * The characters allowed before a hashtag.
     */
    private static final String VALID_CHARS_BEFORE_HASHTAG = "-.,<>()#[]@!$&'()*+,;=% \t\"";

    /**
     * The characters in a valid url.
     */
    private static final String VALID_URL_CHARACTERS = VALID_HASHTAG_CHARS + ".~:/?#[]@!$&'()*+,;=%";

    /**
     * Extract the next hashtag, starting with the input starting position.
     *
     * @param content
     *            the content to search for hashtags
     * @param inStartingIndex
     *            the starting index
     * @return a Substring representing a found hashtag, or null if not found
     */
    public Substring extract(final String content, final int inStartingIndex)
    {
        if (content == null || content.length() == 0 || content.indexOf('#') == -1)
        {
            // no hashtags
            return null;
        }

        int pos = inStartingIndex;

        int hashPos = -1;
        int contentLength = content.length();
        while ((hashPos = content.indexOf('#', pos)) > -1)
        {
            // see if this looks like a hashtag
            if (hashPos == 0 || (isValidCharBeforeHashtag(content.charAt(hashPos - 1))
            // line break
                    && !isInAUrl(content, hashPos) && !isInHrefBlock(content, hashPos)))
            {
                // this is a hashtag

                // walk through this to find its endpoint
                pos = hashPos + 1;
                char nextChar;
                while (pos < contentLength)
                {
                    nextChar = content.charAt(pos);
                    if (!isValidHashTagCharacter(nextChar))
                    {
                        break;
                    }

                    pos++;
                }

                if (pos > hashPos + 1)
                {
                    return new Substring(hashPos, pos - hashPos, content.substring(hashPos, pos));
                }
                // not a hashtag, just a hash
            }
            else
            {
                // not a hashtag
                pos = hashPos + 1;
            }
        }
        return null;
    }

    /**
     * Extract all hashtags from the input content.
     *
     * @param content
     *            the content to extract
     * @return a set of hashtags extracted
     */
    public List<String> extractAll(final String content)
    {
        Set<String> hashTagContents = new HashSet<String>();

        // parse the hashtags content, then send them into the mapper to get them from the database
        Substring hashTag;
        int position = 0;

        while (null != (hashTag = extract(content, position)))
        {
            position = hashTag.getStartIndex() + hashTag.getLength();
            hashTagContents.add(hashTag.getContent());
        }
        return new ArrayList<String>(hashTagContents);
    }

    /**
     * Check if the character at a specific position in content is inside a url.
     *
     * @param content
     *            the content to check
     * @param pos
     *            the position to check
     * @return whether the character at a specific position in content is inside a url
     */
    private boolean isInAUrl(final String content, final int pos)
    {
        // grab the block of potential url text we're currently in
        int blockStartPos = pos;
        for (int p = pos - 1; p >= 0 && VALID_URL_CHARACTERS.indexOf(content.charAt(p)) != -1; p--)
        {
            char c = content.charAt(p);
            if (c == '#')
            {
                // if there's another hash before this hash in the block, it's hashtaggable
                return false;
            }

            blockStartPos = p;
        }
        String block = content.substring(blockStartPos, pos).toLowerCase();
        return block.contains("://") || block.contains("www.");
    }

    /**
     * Test whether the character at the input position is inside a hyperlink tag.
     *
     * @param content
     *            the content to check
     * @param pos
     *            the position to check in the content
     * @return whether the character at the input position is inside a hyperlink tag
     */
    private boolean isInHrefBlock(final String content, final int pos)
    {
        for (int p = pos - 1; p >= 0; p--)
        {
            String block = content.substring(p, pos).toLowerCase();
            if (block.contains("</a>"))
            {
                return false;
            }
            if (block.contains("<a "))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the input character is a valid character right before a hashtag.
     *
     * @param inChar
     *            the char test
     * @return whether the input char is a valid character right before a hashtag
     */
    private boolean isValidCharBeforeHashtag(final char inChar)
    {
        return VALID_CHARS_BEFORE_HASHTAG.indexOf(inChar) != -1;
    }

    /**
     * Check if the input character is a valid hashtag character.
     *
     * @param inChar
     *            the char to check
     * @return whether the input char is valid in a hashtag
     */
    private boolean isValidHashTagCharacter(final char inChar)
    {
        return VALID_HASHTAG_CHARS.indexOf(inChar) > -1;
    }
}
