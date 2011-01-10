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

import com.google.gwt.user.client.Element;

/**
 * This is the interface for common shared untestable code units. Not everything in here is JSNI directly, but all of it
 * calls JSNI at some level so as to make the tests not work. This interface encapsulates that behavior so that it can
 * be mocked.
 */
public interface WidgetJSNIFacade
{

    /**
     * Redirect to a url.
     *
     * @param url
     *            the url.
     */
    void redirectToUrl(final String url);

    /**
     * Gets the currently logged in viewer.
     *
     * @return the id.
     */
    String getViewer();

    /**
     * Sets whether or not the tabs are read only.
     *
     * @param value
     *            the value.
     */
    void setAreTabsReadOnly(boolean value);

    /**
     * Returns whether or not the current tabs are read only.
     *
     * @return the value.
     */
    boolean areTabsReadOnly();

    /**
     * Sets the current theme.
     *
     * @param uuid
     *            the theme uuid.
     */
    void setCurrentTheme(String uuid);

    /**
     * Gets the current theme.
     *
     * @return the current theme uuid.
     */
    String getCurrentTheme();

    /**
     * Sets the banner up top with an image and a color.
     *
     * @param imageUrl
     *            the image url.
     */
    void setBanner(String imageUrl);

    /**
     * Gets the last token in the URL typed by the user, used for figuring out where we are. (e.g. in view/directory
     * return directory)
     *
     * @return the last token.
     */
    // String getLastUrlToken();

    /**
     * Gets the second to last token in the url. (e.g. in view/people/username1 return people)
     *
     * @return second to last token
     */
    String getSecondToLastUrlToken();

    /**
     * Gets a paremeter.
     *
     * @param key
     *            the key of the param.
     * @return the param.
     */
    String getParameter(String key);

    /**
     * Helper class to set the theme css element to the head of the document.
     *
     * @param cssUrl
     *            The url of the css file.
     */
    void setCSS(String cssUrl);

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
    void setAttribute(Element el, String key, String val);

    /**
     * Gets an element by its tag name; handy for single elements like HTML, HEAD, BODY. Here to facilitate themes, GWT
     * doesn't offer a way to insert the &lt;link&gt; element for a theme in the &lt;head&gt; natively.
     *
     * @param tagName
     *            The name of the tag.
     * @return The element with that tag name.
     */
    Element getElementByTagName(String tagName);

    /**
     * Gets an element by its ID. Here to facilitate themes, GWT doesn't offer a way to insert the &lt;link&gt; element
     * for a theme in the &lt;head&gt; natively.
     *
     *
     * @param id
     *            The id of the element.
     * @return The element with that id.
     */
    Element getElementById(String id);

    /**
     * Sets a global window value.
     *
     * @param key
     *            key of the value.
     * @param value
     *            the value.
     */
    void setWindowValue(String key, String value);

    /**
     * Gets a global window value.
     *
     * @param key
     *            key of the value.
     * @return the value.
     */
    String getWindowValue(String key);

    /**
     * Sets the owner of the page.
     *
     * @param ownerId
     *            the owner's ID.
     */
    void setOwner(String ownerId);

    /**
     * Sets the viewer of the page.
     *
     * @param viewerId
     *            the viewer's ID.
     * @param accountId
     *            the NTID of viewer.
     */
    void setViewer(String viewerId, String accountId);

    /**
     * Gets the currently logged in users account id.
     *
     * @return the account id.
     */
    String getCurrentUserAccountId();

    /**
     * Get the history token.
     *
     * @return the history token.
     */
    String getHistoryToken();

    /**
     * Shows the login dialog.
     */
    void showLogin();

    /**
     * Prompt for confirmation.
     *
     * @param prompt
     *            the question to ask.
     * @return the user's response as a boolean.
     */
    boolean confirm(String prompt);

    /**
     * Reload the page.
     */
    void reload();

    /**
     * Add a token to the url to trigger a History event.
     *
     * @param token
     *            - token to add to the url for the history event.
     * @param issueEvent
     *            - suppress history event if false.
     */
    void setHistoryToken(String token, boolean issueEvent);
}
