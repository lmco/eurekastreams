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
package org.eurekastreams.web.client.ui.common.avatar;

import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provides methods to create and display the group coordinator badge on the avatar.
 */
public final class AvatarBadgeManager
{
    /** Singleton. */
    private static AvatarBadgeManager instance = new AvatarBadgeManager();

    /**
     * @return the instance
     */
    public static AvatarBadgeManager getInstance()
    {
        return instance;
    }

    /**
     * Hide constructor.
     */
    private AvatarBadgeManager()
    {
    }

    /**
     * Creates the overlay widget to add to the avatar.
     *
     * @param uniqueId
     *            Unique ID of the avatar's person.
     * @param size
     *            Size.
     * @return Overlay widget.
     */
    public Widget createOverlay(final String uniqueId, final Size size)
    {
        SimplePanel badgeOverlay = new SimplePanel();
        badgeOverlay.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarBadgeOverlay());
        String sizeStyle;
        switch (size)
        {
        case VerySmall:
            sizeStyle = StaticResourceBundle.INSTANCE.coreCss().sizeVerySmall();
            break;
        case Small:
            sizeStyle = StaticResourceBundle.INSTANCE.coreCss().sizeSmall();
            break;
        default:
            sizeStyle = StaticResourceBundle.INSTANCE.coreCss().sizeNormal();
            break;
        }
        badgeOverlay.addStyleName(sizeStyle);
        badgeOverlay.addStyleName("badge-for-" + uniqueId);

        return badgeOverlay;
    }

    /**
     * Enables badges for a user.
     *
     * @param scopingStyle
     *            Style in which badges should be displayed.
     * @param uniqueId
     *            Unique ID of user.
     */
    public void setBadge(final String scopingStyle, final String uniqueId)
    {
        nativeSetBadge(scopingStyle, uniqueId);
    }

    /**
     * Creates a badge rule for a user.
     *
     * @param scopingStyle
     *            Style in which badges should be displayed.
     * @param uniqueId
     *            Unique ID of user.
     */
    private static native void nativeSetBadge(final String scopingStyle, final String uniqueId)
    /*-{
           var sheet;
           var selector = (scopingStyle ? '.' + scopingStyle + ' ' : '') + '.badge-for-' + uniqueId;
           if (!$wnd.avatarBadgeManagerStylesheet)
           {
               @org.eurekastreams.web.client.ui.common.avatar.AvatarBadgeManager::nativeCreateStylesheet()();
           }
           sheet = $wnd.avatarBadgeManagerStylesheet;
           if (sheet.insertRule)
               sheet.insertRule(selector + ' { display: block; }', 0);
           else
               sheet.addRule(selector, 'display: block', -1);
    }-*/;

    /**
     * Clears all avatar badges.
     */
    public void clearBadges()
    {
        nativeClearBadges();
    }

    /**
     * Deletes all the dynamically-generated badge rules.
     */
    private static native void nativeClearBadges()
    /*-{
           var sheet;
           if (!$wnd.avatarBadgeManagerStylesheet)
           {
               @org.eurekastreams.web.client.ui.common.avatar.AvatarBadgeManager::nativeCreateStylesheet()();
           }
           sheet = $wnd.avatarBadgeManagerStylesheet;
           if (sheet.cssRules)
           {
                while (sheet.cssRules.length)
                    sheet.deleteRule(0);
           }
           else
           {
                while (sheet.rules.length)
                    sheet.removeRule(0);
           }
       }-*/;

    /**
     * Creates the dynamically-generated stylesheet for the coordinator rules.
     */
    private static native void nativeCreateStylesheet()
    /*-{
            var node = $doc.createElement('style');
            node.type = "text/css";
            node.title = "AvatarBadgeManagerStyles";
            $doc.getElementsByTagName("head")[0].appendChild(node);
            var i;
            for (i=0; i < $doc.styleSheets.length; i++)
            {
                if ($doc.styleSheets[i].title === 'AvatarBadgeManagerStyles')
                {
                    $wnd.avatarBadgeManagerStylesheet = $doc.styleSheets[i];
                    break;
                }
            }
        }-*/;
}
