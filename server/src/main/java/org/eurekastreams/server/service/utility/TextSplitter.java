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
package org.eurekastreams.server.service.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Breaks a text string into multiple segments based on size.
 */
public class TextSplitter
{
    /** Maximum size of first piece. */
    private final int firstBlockSize;

    /** Maximum size of remaining pieces. */
    private final int blockSize; // decrement by 3

    /** Text placed at start of a continued piece. */
    private final String continuedFromMark;

    /** Text placed at end of a piece to be continued. */
    private final String continueToMark;

    /** Length of continueToMark. */
    private final int continueToMarkLength;

    /**
     * Constructor.
     *
     * @param inFirstBlockSize
     *            Maximum size of first piece.
     * @param inBlockSize
     *            Maximum size of remaining pieces.
     */
    public TextSplitter(final int inFirstBlockSize, final int inBlockSize)
    {
        this(inFirstBlockSize, inBlockSize, "...", "...");
    }

    /**
     * Constructor.
     *
     * @param inFirstBlockSize
     *            Maximum size of first piece.
     * @param inBlockSize
     *            Maximum size of remaining pieces.
     * @param inContinuedFromMark
     *            Text placed at start of a continued piece.
     * @param inContinueToMark
     *            Text placed at end of a piece to be continued.
     */
    public TextSplitter(final int inFirstBlockSize, final int inBlockSize, final String inContinuedFromMark,
            final String inContinueToMark)
    {
        firstBlockSize = inFirstBlockSize;
        blockSize = inBlockSize - inContinuedFromMark.length();
        continuedFromMark = inContinuedFromMark;
        continueToMark = inContinueToMark;
        continueToMarkLength = continueToMark.length();
    }

    /**
     * Splits a string into multiple pieces on size and places continuation marks on them.
     *
     * @param originalInputText
     *            Single string to split.
     * @return List of pieces.
     */
    public List<String> split(final String originalInputText)
    {
        List<String> piecesList = new ArrayList<String>();

        int startIdx = 0;
        String inputText = originalInputText.trim();
        int inputLength = inputText.length();
        while (startIdx < inputLength)
        {
            // skip any whitespace at start of a piece
            if (Character.isWhitespace(inputText.charAt(startIdx)))
            {
                startIdx++;
                continue;
            }

            int useBlockSize = piecesList.isEmpty() ? firstBlockSize : blockSize;
            int remainingSize = inputLength - startIdx;
            if (remainingSize > useBlockSize)
            {
                // look backward from the end of the block for a break
                int scanIdx = startIdx + useBlockSize - continueToMarkLength;
                while (scanIdx > startIdx && !Character.isWhitespace(inputText.charAt(scanIdx)))
                {
                    scanIdx--;
                }
                // if no break found, just chop mid-word
                if (scanIdx == startIdx)
                {
                    scanIdx = startIdx + useBlockSize - continueToMarkLength;
                }

                // build text segment
                // Note: trim off any extra whitespace at end (in case the break found was multiple chars long)
                piecesList.add((piecesList.isEmpty() ? "" : continuedFromMark)
                        + inputText.substring(startIdx, scanIdx).trim() + continueToMark);

                // move to next start position
                startIdx = scanIdx;
            }
            else
            {
                piecesList.add((piecesList.isEmpty() ? "" : continuedFromMark) + inputText.substring(startIdx));
                break;
            }
        }

        return piecesList;
    }
}
