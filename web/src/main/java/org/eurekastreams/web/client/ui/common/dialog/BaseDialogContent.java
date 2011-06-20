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
package org.eurekastreams.web.client.ui.common.dialog;



/**
 * Base dialog content. Allows boilerplate code to not need to be reimplemented.
 */
public abstract class BaseDialogContent implements DialogContent
{
    /** Hosting dialog. */
    private DialogContentHost hostDialog;

    /**
     * {@inheritDoc}
     */
    public void setHost(final DialogContentHost inDialog)
    {
        hostDialog = inDialog;
    }

    /**
     * Tells the host dialog to close itself.
     */
    public void close()
    {
        hostDialog.hide();
    }

    /**
     * Tells the host dialog to recenter itself (after content has changed size).
     */
    protected void recenter()
    {
        hostDialog.center();
    }

    /**
     * Provides a do-nothing implementation of show for dialogs which do not need to take any action on show.
     */
    public void show()
    {
    }

    /**
     * Provides a do-nothing implementation of show for dialogs which do not need to take any action on hide.
     */
    public void beforeHide()
    {
    }

    /**
     * @return Returns a do-nothing implementation for dialogs which do not need to add a style.
     */
    public String getCssName()
    {
        return null;
    }

}
