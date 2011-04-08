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
package org.eurekastreams.web.client.jsni;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.web.client.ui.common.dialog.DialogFactory;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;

/**
 * The implementation of the WidgetJSNIFacade interface. It does things we shouldn't have to do but need to do here
 * because of limitations in jUnit and GWT. See the interface for a better explanation.
 */
public class WidgetJSNIFacadeImpl implements WidgetJSNIFacade
{
    /**
     * Escapes HTML.
     *
     * @param maybeHtml
     *            some text that might have HTML tags in it.
     * @return an escaped version of the text.
     */
    public String escapeHtml(final String maybeHtml)
    {
        final Element div = DOM.createDiv();
        DOM.setInnerText(div, maybeHtml);
        return DOM.getInnerHTML(div);
    }

    /**
     * Helper class to set the theme css element to the head of the document.
     *
     * @param inCssUrl
     *            The url of the css file.
     */
    public void setCSS(final String inCssUrl)
    {
        String cssUrl = inCssUrl;

        Element head = getElementByTagName("head");
        Element cssLink = getElementById("theme");

        if (null == cssLink)
        {
            cssLink = DOM.createElement("link");
            DOM.appendChild(head, cssLink);
        }
        else
        {
            // Setting a theme, need to froce the cache to clear.
            cssUrl += "?forceReload";
        }

        setAttribute(cssLink, "type", "text/css");
        setAttribute(cssLink, "rel", "stylesheet");
        setAttribute(cssLink, "href", cssUrl);
        setAttribute(cssLink, "id", "theme");

    }

    /**
     * JSNI Method to set the attribute of an element. Here to facilitate themes, GWT doesn't offer a way to insert the
     * &lt;link&gt; element for a theme in the &lt;head&gt; natively.
     *
     * @param el
     *            The element to modify the attribute of.
     * @param key
     *            The key, or name of the attribute.
     * @param val
     *            The value of the attribute.
     */
    public void setAttribute(final Element el, final String key, final String val)
    {
        nativeSetAttribute(el, key, val);
    }

    /**
     * JSNI Method to set the attribute of an element. Here to facilitate themes, GWT doesn't offer a way to insert the
     * &lt;link&gt; element for a theme in the &lt;head&gt; natively.
     *
     * @param el
     *            The element to modify the attribute of.
     * @param key
     *            The key, or name of the attribute.
     * @param val
     *            The value of the attribute.
     */
    public static native void nativeSetAttribute(final Element el, final String key, final String val)
    /*-{
    el.setAttribute(key, val);
    }-*/;

    /**
     * Gets an element by its tag name; handy for single elements like HTML, HEAD, BODY. Here to facilitate themes, GWT
     * doesn't offer a way to insert the &lt;link&gt; element for a theme in the &lt;head&gt; natively.
     *
     * @param tagName
     *            The name of the tag.
     * @return The element with that tag name.
     */
    public Element getElementByTagName(final String tagName)
    {
        return nativeGetElementByTagName(tagName);
    }

    /**
     * Gets an element by its tag name; handy for single elements like HTML, HEAD, BODY. Here to facilitate themes, GWT
     * doesn't offer a way to insert the &lt;link&gt; element for a theme in the &lt;head&gt; natively.
     *
     * @param tagName
     *            The name of the tag.
     * @return The element with that tag name.
     */
    public static native Element nativeGetElementByTagName(final String tagName)
    /*-{
    var elem = $doc.getElementsByTagName(tagName);
    return elem ? elem[0] : null;
    }-*/;

    /**
     * Gets an element by its ID. Here to facilitate themes, GWT doesn't offer a way to insert the &lt;link&gt; element
     * for a theme in the &lt;head&gt; natively.
     *
     *
     * @param id
     *            The id of the element.
     * @return The element with that id.
     */
    public Element getElementById(final String id)
    {
        return nativeGetElementById(id);
    }

    /**
     * Encode a string for use in URL.
     *
     * @param value
     *            the value to encode.
     * @return the encoded string.
     */
    public String urlEncode(final String value)
    {
        return URL.encodeComponent(value);
    }

    /**
     * Decode a URL encoded string.
     *
     * @param value
     *            the value to decode.
     * @return the decoded string.
     */
    public String urlDecode(final String value)
    {
        return URL.decodeComponent(value);
    }

    /**
     * Gets an element by its ID. Here to facilitate themes, GWT doesn't offer a way to insert the &lt;link&gt; element
     * for a theme in the &lt;head&gt; natively.
     *
     *
     * @param id
     *            The id of the element.
     * @return The element with that id.
     */
    public static native Element nativeGetElementById(final String id) /*-{
                                                                         var elem = $doc.getElementById(id);
                                                                         return elem;
                                                                       }-*/;

    /**
     * Get the history token.
     *
     * @return the history token.
     */
    public String getHistoryToken()
    {
        return History.getToken();
    }

    /**
     * Shows the login dialog.
     */
    public void showLogin()
    {
        final WidgetCommand loginDialog = DialogFactory.getDialog("login", null);
        loginDialog.execute();
    }

    /**
     * Prompt for confirmation.
     *
     * @param prompt
     *            the question to ask.
     * @return the user's response as a boolean.
     */
    public boolean confirm(final String prompt)
    {
        return Window.confirm(prompt);
    }

    /**
     * Reload the page.
     */
    public void reload()
    {
        History.newItem("");
        Window.Location.reload();
    }

    /**
     * Gets a paremeter.
     *
     * @param key
     *            the key of the param.
     * @return the param.
     */
    public String getParameter(final String key)
    {
        return Window.Location.getParameter(key);
    }

    // /**
    // * @see WidgetJSNIFacade
    // * @return the last token.
    // */
    // public String getLastUrlToken()
    // {
    // String[] fullUrlTokens = Window.Location.getHref().split("#");
    // if (fullUrlTokens.length > 1)
    // {
    // String[] urlTokens = fullUrlTokens[1].split("/");
    // String[] views =
    // }
    // return urlTokens[urlTokens.length - 1];
    // }

    /**
     * Sets a global window value.
     *
     * @param key
     *            key of the value.
     * @param value
     *            the value.
     */
    public void setWindowValue(final String key, final String value)
    {
        nativeSetWindowValue(key, value);
    }

    /**
     * Sets the owner of the page.
     *
     * @param ownerId
     *            the owner's ID.
     */
    public void setOwner(final String ownerId)
    {
        setWindowValue("OWNER", ownerId);
    }

    /**
     * Add a history listener.
     *
     * @param listener
     *            the listener.
     */
    public void addHistoryListener(final HistoryListener listener)
    {
        History.addHistoryListener(listener);
    }

    /**
     * Sets the viewer of the page.
     *
     * @param viewerId
     *            the viewer's ID.
     * @param accountId
     *            the account id.
     */
    public void setViewer(final String viewerId, final String accountId)
    {
        setWindowValue("VIEWER", viewerId);
        setWindowValue("ACCOUNTID", accountId);
    }

    /**
     * Sets a global window value.
     *
     * @param key
     *            key of the value.
     * @param value
     *            the value.
     */
    public static native void nativeSetWindowValue(final String key, final String value) /*-{
                                                                                         $wnd[key] = value;
                                                                                         }-*/;

    /**
     * {@inheritDoc}
     */
    public String getWindowValue(final String inKey)
    {
        return nativeGetWindowValue(inKey);
    }

    /**
     * Gets the value from the key.
     *
     * @param key
     *            the key.
     * @return the value.
     */
    public static native String nativeGetWindowValue(final String key) /*-{
                                                                       return $wnd[key];
                                                                       }-*/;

    /**
     * @see WidgetJSNIFacade
     * @return the second to last token.
     */
    public String getSecondToLastUrlToken()
    {
        String[] fullUrlTokens = Window.Location.getHref().split("#");
        String[] urlTokens = fullUrlTokens[0].split("/");

        String secondToLastToken = "";

        if (urlTokens.length >= 2)
        {
            secondToLastToken = urlTokens[urlTokens.length - 2];
        }

        return secondToLastToken;

    }

    /**
     * {@inheritDoc}
     */
    public void setHistoryToken(final String token, final boolean issueEvent)
    {
        History.newItem(token, issueEvent);
    }

    /**
     * Sets the banner up top with an image and a color.
     *
     * @param imageUrl
     *            the image url.
     */
    public void setBanner(final String imageUrl)
    {
        nativeSetBanner(imageUrl);
    }

    /**
     * JSNI to set banner.
     *
     * @param url
     *            the url of the image.
     */
    public static native void nativeSetBanner(final String url) /*-{
                                                                var elem = $doc.getElementById("banner");
                                                                elem.style.backgroundRepeat="no-repeat";
                                                                elem.style.backgroundImage="url("+url+")";
                                                                }-*/;

    /**
     * Clear the banner when none is specified.
     *
     * @param inBlankBanner
     *            - flag to indicate whether a blank banner should be displayed.
     */
    public void clearBanner(final boolean inBlankBanner)
    {
        nativeClearBanner(inBlankBanner);
    }

    /**
     * JSNI to clear the banner.
     *
     * @param inBlankBanner
     *            - flag to indicate whether to show a blank banner or remove the banner styling altogether and allow
     *            any underlying background image to show through.
     *
     */
    public static native void nativeClearBanner(final boolean inBlankBanner)
    /*-{
         var elem = $doc.getElementById("banner");
         if(inBlankBanner)
         {
             elem.style.backgroundImage="none";
         }
         else
         {
             elem.style.backgroundImage="";
         }
     }-*/;

    /**
     * Gets the current theme.
     *
     * @return the current theme.
     */
    public String getCurrentTheme()
    {
        return nativeGetCurrentTheme();
    }

    /**
     * Native gets the current theme.
     *
     * @return the current theme.
     */
    public static native String nativeGetCurrentTheme() /*-{
                                                        return $wnd.currentThemeUuid;
                                                        }-*/;

    /**
     * Sets the current theme uuid.
     *
     * @param uuid
     *            the theme uuid.
     */
    public void setCurrentTheme(final String uuid)
    {
        nativeSetCurrentTheme(uuid);
    }

    /**
     * Sets the current theme uuid.
     *
     * @param uuid
     *            the theme uuid.
     */
    public static native void nativeSetCurrentTheme(final String uuid)
    /*-{
        $wnd.currentThemeUuid = uuid;
    }-*/;

    /**
     * Sets whether or not the tabs are read only.
     *
     * @param value
     *            the value.
     */
    public void setAreTabsReadOnly(final boolean value)
    {
        nativeSetAreTabsReadOnly(value);
    }

    /**
     * Returns whether or not the current tabs are read only.
     *
     * @return the value.
     */
    public boolean areTabsReadOnly()
    {
        return nativeGetAreTabsReadOnly();
    }

    /**
     * Returns whether or not the current tabs are read only.
     *
     * @return the value.
     */
    public static native boolean nativeGetAreTabsReadOnly()
    /*-{
        return $wnd.areTabsReadOnly;
    }-*/;

    /**
     * Sets whether or not the tabs are read only.
     *
     * @param value
     *            the value.
     */
    public static native void nativeSetAreTabsReadOnly(final boolean value)
    /*-{
        $wnd.areTabsReadOnly = value;
    }-*/;

    /**
     * Gets the currently logged in viewer.
     *
     * @return the id.
     */
    public String getViewer()
    {
        return nativeGetViewer();
    }

    /**
     * Gets the currently logged in viewer.
     *
     * @return the id.
     */
    public static native String nativeGetViewer()
    /*-{
        return $wnd["VIEWER"];
    }-*/;

    /**
     * Redirect to a url.
     *
     * @param url
     *            the url.
     */
    public void redirectToUrl(final String url)
    {
        Window.Location.assign(url);
    }

    /**
     * Gets the current user account id.
     *
     * @return the account id.
     */
    public String getCurrentUserAccountId()
    {
        return nativeGetWindowValue("ACCOUNTID");
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        nativeClose();
    }

    /**
     * Close the current window.
     */
    public static native void nativeClose() /*-{ $wnd.close(); }-*/;

    /**
     * Builds the portion of the Window.open feature string to center a dialog over the current window.
     *
     * @param width
     *            Dialog width.
     * @param height
     *            Dialog height.
     * @return Feature string fragment.
     */
    public String getCenteredPopupFeatureString(final int width, final int height)
    {
        return nativeGetCenteredPopupFeatureString(width, height);
    }

    /**
     * Builds the portion of the Window.open feature string to center a dialog over the current window.
     *
     * @param width
     *            Dialog width.
     * @param height
     *            Dialog height.
     * @return Feature string fragment.
     */
    public static native String nativeGetCenteredPopupFeatureString(final int width, final int height)
    /*-{
        var top = $wnd.screenY + ($wnd.outerHeight - height) / 2;
        var left = $wnd.screenX + ($wnd.outerWidth - width) / 2;
        return "width=" + width + ",height=" + height + ",left=" + left + ",top=" + top;
    }-*/;
}
