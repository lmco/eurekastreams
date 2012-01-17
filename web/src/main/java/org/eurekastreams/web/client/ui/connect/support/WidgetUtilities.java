/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.support;

import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;

import com.google.gwt.user.client.Window;

/**
 * Utilities / common code for widgets.
 */
public final class WidgetUtilities
{
    /** Forbid instantiation. */
    private WidgetUtilities()
    {
    }

    /**
     * Builds the URL for a widget to pop up an activity share box.
     *
     * @param activityId
     *            Activity ID.
     * @return URL (relative).
     */
    public static String getShareActivityPopupUrl(final long activityId)
    {
        return "?widget=shareactivitydialog&activityid=" + activityId;
    }

    /**
     * Pops up an activity share box from a widget.
     *
     * @param activityId
     *            Activity ID.
     */
    public static void showShareActivityPopup(final long activityId)
    {
        final int width = 650;
        final int height = 340;
        Window.open(
                WidgetUtilities.getShareActivityPopupUrl(activityId),
                "_blank",
                "menubar=no,status=no,toolbar=no,location=no,"
                + WidgetJSNIFacadeImpl.nativeGetCenteredPopupFeatureString(width, height));
    }
}
