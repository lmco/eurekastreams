/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.jsni;

import com.google.gwt.dom.client.Element;

/**
 * Renders a gadget.
 *
 */
public class GadgetRenderer
{

    /**
     * Singleton.
     */
    private static GadgetRenderer gadgetRenderer = new GadgetRenderer();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static GadgetRenderer getInstance()
    {
        return gadgetRenderer;
    }

    // TODO change the gadget.js implementation to only need the gadget ID. We
    // don't need to send it both the ID and the chromeID anymore. If we store
    // the prefix for chrome ID as a constant then we should know what it is.
    /**
     * Will Register a Single Gadget in the container and render it.
     *
     * @param chromeId
     *            the div id where you want to gadget rendered at
     * @param url
     *            Gadget xml url path
     * @param id
     *            the gadgets id modifier.
     * @param gadgetDefId
     *            the gadget definition id.
     * @param userPrefs
     *            String based JSON representation of the gadget's user preferences.
     */
    public final void registerSingleGadgetInContainer(final String chromeId, final String url, final Long id,
            final Long gadgetDefId, final String userPrefs)
    {
        registerSingleGadgetInContainer(chromeId, url, id, "home", gadgetDefId, userPrefs);
    }

    /**
     * Clear the container.
     */
    public void clearGadgetContainer()
    {
        clearGadgetContainerNative();
    }

    /**
     * Change the container view.
     *
     * @param view
     *            the view.
     */
    public void changeContainerView(final String view)
    {
        changeContainerViewNative(view);
    }

    /**
     * Will Register a Single Gadget in the container and render it. called when specifying a view
     *
     * @param chromeId
     * @param url
     * @param view
     *            (canvas, home, etc. null for default)
     * @param chromeId
     *            id of the chrome.
     * @param url
     *            the url of the gadget.
     * @param id
     *            the gadgets id modifier.
     * @param gadgetDefId
     *            the gadget definition id.
     * @param userPrefs
     *            String based JSON representation of the gadget's user preferences.
     */
    public final native void registerSingleGadgetInContainer(final String chromeId, final String url, final Long id,
            final String view, final Long gadgetDefId, final String userPrefs)
    /*-{
        var gadget = $wnd.gadgets.container.getGadget(id);

        gadget = $wnd.gadgets.container.createGadget({specUrl: url, title:'', appId: gadgetDefId});
        if(userPrefs != null && userPrefs != "")
        {
            gadget.userPrefs_ = $wnd.gadgets.json.parse(userPrefs);
        }
        gadget.setServerBase('/gadgets/');
        gadget.secureToken = escape($wnd.eurekastreams.container.generateSecureToken(
            $wnd.OWNER, $wnd.VIEWER, gadgetDefId, url));
        $wnd.gadgets.container.setView(view);
        $wnd.gadgets.container.addGadgetWithId(gadget,id);

    }-*/;


    /**
     * JSNI Method to find the gadgetZone and apply the maximize style to it.
     *
     * @param gadgetZone
     *            - gadgetZone to maximize.
     */
    public native void maximizeGadgetZone(final Element gadgetZone)
    /*-{
       $wnd.jQuery('.empty-zone').hide();
       $wnd.jQuery('.gadget-zone').hide();
       $wnd.jQuery('.maximized').removeClass('maximized');
       $wnd.jQuery(gadgetZone).addClass('maximized').show();

    }-*/;

    /**
     * JSNI method to find remove the maximize style from the maximized zone and restore the hidden gadget zones.
     */
    public native void restoreGadgetZone()
    /*-{
       $wnd.jQuery('.empty-zone').show();
       $wnd.jQuery('.gadget-zone').show();
       $wnd.jQuery('.maximized').removeClass('maximized');
       $wnd.jQuery('.gadget-zone.hidden').hide();
    }-*/;


    /**
     * Calls the default open Preference method provided by shindig.
     *
     * @param moduleID
     *            moduleID of a gadget
     */
    public native void openPreferences(final String moduleID)
    /*-{
       $wnd.gadgets.container.getGadget(moduleID).handleOpenUserPrefsDialog();
    }-*/;

    /**
     * JSNI method to call the containers refresh on a gadget.
     *
     * @param moduleID
     *            moduleID of a gadget
     */
    public native void refreshGadget(final String moduleID)
    /*-{
      $wnd.gadgets.container.getGadget(moduleID).refresh();
    }-*/;


    /**
     * Add the gadget chome.
     *
     * @param chromeId
     *            the chome id.
     * @param id
     *            the gadget id.
     */
    public final native void addGadgetChrome(final String chromeId, final Long id)
    /*-{
        $wnd.gadgets.container.layoutManager.addGadgetChromeId(chromeId, id);
    }-*/;

    /**
     * uses the gadget container to render a given gadget. As opposed to calling the gadgets render method which does
     * not seem to work correctly.
     *
     * @param moduleID
     *            moduleID of a gadget
     */
    public final native void renderGadget(final String moduleID)
    /*-{
      $wnd.gadgets.container.renderGadget($wnd.gadgets.container.getGadget(moduleID));
    }-*/;

    /**
     * This method tells the gadget container to update the supplied gadget's render location. This is mainly intended
     * for view changes when a gadget has already been rendered. Since the render method constructs an entirely new
     * iframe within the chrome it is more heavyweight than necessary. This method will just update the location of the
     * already created iframe.
     *
     * @param moduleId
     *            moduleId of the gadget to update the RendererUrl.
     * @param viewParams
     *            parameters passed from the gadget requesting the iframe to be refreshed.
     */
    public final native void refreshGadgetIFrameUrl(final String moduleId, final String viewParams)
    /*-{
        $wnd.gadgets.container.refreshGadgetIFrameUrl($wnd.gadgets.container.getGadget(moduleId), viewParams);
    }-*/;

    /**
     * Message to display when a gadget IFrameUrl is being refreshed. This will trigger the waiting message to be
     * displayed.
     *
     * @param moduleId
     *            - id of the gadget that is being updated.
     */
    public final native void gadgetIFrameUrlRefreshing(final String moduleId)
    /*-{
        $wnd.gadgets.container.gadgetIFrameUrlRefreshing($wnd.gadgets.container.getGadget(moduleId));
    }-*/;

    /**
     * Changes the containers view.
     *
     * @param view
     *            the view to change to.
     */
    public final native void changeContainerViewNative(final String view)
    /*-{
      $wnd.gadgets.container.setView(view);
    }-*/;

    /**
     * This method will clear the gadgets in the js gadget container.
     */
    public final native void clearGadgetContainerNative()
    // TODO this clears the container but does not reset the gadgetIDs.
    // gadgetIDs is an incrementor of gadgets. it also appears to be how
    // prefeances are retrieved
    /*-{
      $wnd.gadgets.container.layoutManager.gadgetChromeIds_ = [];
      $wnd.gadgets.container.gadgets_={};
    }-*/;

    /**
     * This method will refresh all in the js gadget container.
     */
    public final native void refreshGadgetsInContainer()
    // TODO this is untested.
    /*-{
      $wnd.gadgets.container.refreshGadgets();
    }-*/;

    /**
     * This method will render all in the js gadget container.
     */
    public final native void renderGagdets()
    /*-{
      $wnd.gadgets.container.renderGadgets();
    }-*/;

}
