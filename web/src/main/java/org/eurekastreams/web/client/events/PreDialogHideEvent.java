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
package org.eurekastreams.web.client.events;

import org.eurekastreams.web.client.ui.common.dialog.Dialog;

/**
 * Occurs before the dialog has been hidden.
 */
public class PreDialogHideEvent
{
    /**
     * The dialog.
     */
    private Dialog dialog;

    /**
     * Default constructor.
     * 
     * @param inDialog
     *            dialog.
     */
    public PreDialogHideEvent(final Dialog inDialog)
    {
        dialog = inDialog;
    }

    /**
     * Get the dialog.
     * 
     * @return the dialog.
     */
    public Dialog getDialog()
    {
        return dialog;
    }
}
