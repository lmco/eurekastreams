/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.dialog;

import org.eurekastreams.web.client.ui.common.dialog.login.LoginDialogContent;

/**
 * Used to obtain dialogs.
 */
public final class DialogFactory
{
    /** Prevent instantiation. */
    private DialogFactory()
    {
    }

    /**
     * Gets a type of dialog.
     *
     * @param inDialogType
     *            the type of dialog requested.
     * @return the requested dialog.
     */
    public static Dialog getDialog(final String inDialogType)
    {
        DialogContent content;
        if ("login".equals(inDialogType))
        {
            content =new LoginDialogContent();
        }
        else
        {
            throw new IllegalArgumentException("Unrecognized dialog type '" + inDialogType + "' requested.");
        }

        return new Dialog(content);
    }
}
