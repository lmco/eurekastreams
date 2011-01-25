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
/*
 * The following file is taken from the Apache Tomahawk Sandbox and incorporated here so that Eureka Streams would not
 * need to depend on an entire library for one method or rely on sandbox project.  (The project pom.xml states "there
 * is no guarantee of API stability or continued maintenance for any code that is in the sandbox".)  Modifications
 * were made to comply with the Eureka Stream checkstyle rules.  The source was retrieved on 1/25/2011 from the folder
 * http://svn.apache.org/repos/asf/myfaces/tomahawk/trunk/sandbox/core/src/main/java/org/apache/myfaces/custom/util
 * The original copyright notice appears below.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.custom.util;

/**
 * This utility class provides an equivalent to the JavaScript decodeURIComponent(encodedURI) function.
 *
 * @author Gerald M\u00FCllan Date: 11.02.2007 Time: 23:30:08
 */
public final class URIComponentUtils
{
    /** For UTF-8 decoding. */
    private static final int MASK_MATCH_F8 = 0xf8;

    /** For UTF-8 decoding. */
    private static final int MASK_MATCH_F0 = 0xf0;

    /** For UTF-8 decoding. */
    private static final int MASK_MATCH_E0 = 0xe0;

    /** For UTF-8 decoding. */
    private static final int MASK_MATCH_C0 = 0xc0;

    /** For UTF-8 decoding. */
    private static final int MASK_MATCH_80 = 0x80;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_3F = 0x3f;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_0F = 0x0f;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_FC = 0xfc;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_F8 = 0xf8;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_F0 = 0xf0;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_1F = 0x1f;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_80 = 0x80;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_E0 = 0xe0;

    /** For UTF-8 decoding. */
    private static final int UTF8_MASK_C0 = 0xc0;

    /** For converting hexadecimal digits. */
    private static final int HEX_DIGIT_MASK = 0xF;

    /** For converting hexadecimal digits. */
    private static final char HEX_LETTER_OFFSET = 10;

    /** Hide constructor to prevent instantiation. */
    private URIComponentUtils()
    {
    }

    /**
     * In case of incoming Strings - encoded with js encodeURIComponent() e.g. %C3%B6 - it is not sufficient to set
     * CharacterEncoding on the ResponseWriter accordingly. Passing the uri to decodeURIComponent(String encodedURI)
     * decodes e.g. %C3%B6 back to o-umlaut.
     *
     * @return decoded uri String
     * @param encodedURI
     *            uri String
     */
    public static String decodeURIComponent(final String encodedURI)
    {
        char actualChar;

        StringBuffer buffer = new StringBuffer();

        int bytePattern, sumb = 0;

        for (int i = 0, more = -1; i < encodedURI.length(); i++)
        {
            actualChar = encodedURI.charAt(i);

            switch (actualChar)
            {
            case '%':
                actualChar = encodedURI.charAt(++i);
                int hb = (Character.isDigit(actualChar) ? actualChar - '0' : Character.toLowerCase(actualChar) - 'a'
                        + HEX_LETTER_OFFSET)
                        & HEX_DIGIT_MASK;
                actualChar = encodedURI.charAt(++i);
                int lb = (Character.isDigit(actualChar) ? actualChar - '0' : Character.toLowerCase(actualChar) - 'a'
                        + HEX_LETTER_OFFSET)
                        & HEX_DIGIT_MASK;
                bytePattern = (hb << 4) | lb;
                break;
            case '+':
                bytePattern = ' ';
                break;
            default:
                bytePattern = actualChar;
            }

            // Decode byte bytePattern as UTF-8, sumb collects incomplete chars
            if ((bytePattern & UTF8_MASK_C0) == MASK_MATCH_80)
            { // 10xxxxxx
                sumb = (sumb << 6) | (bytePattern & UTF8_MASK_3F);
                if (--more == 0)
                {
                    buffer.append((char) sumb);
                }
            }
            else if ((bytePattern & UTF8_MASK_80) == 0x00)
            { // 0xxxxxxx
                buffer.append((char) bytePattern);
            }
            else if ((bytePattern & UTF8_MASK_E0) == MASK_MATCH_C0)
            { // 110xxxxx
                sumb = bytePattern & UTF8_MASK_1F;
                more = 1;
            }
            else if ((bytePattern & UTF8_MASK_F0) == MASK_MATCH_E0)
            { // 1110xxxx
                sumb = bytePattern & UTF8_MASK_0F;
                more = 2;
            }
            else if ((bytePattern & UTF8_MASK_F8) == MASK_MATCH_F0)
            { // 11110xxx
                sumb = bytePattern & 0x07;
                more = 3;
            }
            else if ((bytePattern & UTF8_MASK_FC) == MASK_MATCH_F8)
            { // 111110xx
                sumb = bytePattern & 0x03;
                more = 4;
            }
            else
            { // 1111110x
                sumb = bytePattern & 0x01;
                more = 5;
            }
        }
        return buffer.toString();
    }
}
