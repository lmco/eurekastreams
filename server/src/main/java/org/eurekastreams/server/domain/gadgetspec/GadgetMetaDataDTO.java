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
package org.eurekastreams.server.domain.gadgetspec;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.GeneralGadgetDefinition;


/**
 * A wrapper class for the gadget meta data.
 *
 */
public class GadgetMetaDataDTO implements Serializable
{
    /**
     * The title.
     */
    private String title;
    /**
     * The URL of the gadget. Called titleUrl because of the gadget spec.
     */
    private String titleUrl;
    /**
     * The description.
     */
    private String description;
    /**
     * The author.
     */
    private String author;
    /**
     * The author email.
     */
    private String authorEmail;
    /**
     * The thumbnail.
     */
    private String thumbnail;
    /**
     * The screenshot.
     */
    private String screenshot;
    /**
     * The summary string..
     */
    private String gadgetString;
    /**
     * The gadget def.
     */
    private GeneralGadgetDefinition gadgetDefinition;

    /**
     * List of User Preferences for the current gadget.
     */
    private List<UserPrefDTO> userPrefs;

    /**
     * List of features the gadget has installed.
     */
    private List<String> features;

    /**
     * Creates a gadget meta data from a gadget def.
     *
     * @param inGadgetDefinition
     *            the gadget def.
     */
    public GadgetMetaDataDTO(final GeneralGadgetDefinition inGadgetDefinition)
    {
        this.gadgetDefinition = inGadgetDefinition;
    }

    /**
     * Returns the gadget def.
     *
     * @return the gadget def.
     */
    public GeneralGadgetDefinition getGadgetDefinition()
    {
        return gadgetDefinition;
    }

    /**
     * Returns the title.
     *
     * @return the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param inTitle
     *            the title.
     */
    public void setTitle(final String inTitle)
    {
        this.title = inTitle;
    }

    /**
     * Sets the title url.
     *
     * @param inTitleUrl
     *            the title url.
     */
    public void setTitleUrl(final String inTitleUrl)
    {
        this.titleUrl = inTitleUrl;
    }

    /**
     * Gets the title url.
     *
     * @return the title url.
     */
    public String getTitleUrl()
    {
        return titleUrl;
    }

    /**
     * Sets the description.
     *
     * @param inDescription
     *            the description.
     */
    public void setDescription(final String inDescription)
    {
        this.description = inDescription;
    }

    /**
     * Gets the description.
     *
     * @return the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the author.
     *
     * @param inAuthor
     *            the author.
     */
    public void setAuthor(final String inAuthor)
    {
        this.author = inAuthor;
    }

    /**
     * Gets the author.
     *
     * @return the author.
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Sets the author email.
     *
     * @param inAuthorEmail
     *            the author email.
     */
    public void setAuthorEmail(final String inAuthorEmail)
    {
        this.authorEmail = inAuthorEmail;
    }

    /**
     * Gets the author email.
     *
     * @return the author email.
     */
    public String getAuthorEmail()
    {
        return authorEmail;
    }

    /**
     * Sets the thumbnail.
     *
     * @param inThumbnail
     *            the thumbnail.
     */
    public void setThumbnail(final String inThumbnail)
    {
        this.thumbnail = inThumbnail;
    }

    /**
     * Gets the thumbnail.
     *
     * @return the thumbnail.
     */
    public String getThumbnail()
    {
        return thumbnail;
    }
    /**
     * Sets the screenshot.
     *
     * @param inScreenshot
     * 			  the screenshot.
     */
    public void setScreenshot(final String inScreenshot)
    {
        this.screenshot = inScreenshot;
    }

    /**
     * Gets the screenshot.
     *
     * @return the screenshot.
     */
    public String getScreenshot()
    {
        return screenshot;
    }

    /**
     * Sets the string.
     *
     * @param inString
     *            the string.
     */
    public void setString(final String inString)
    {
        this.gadgetString = inString;
    }

    /**
     * Gets the string.
     *
     * @return the string.
     */
    public String getString()
    {
        return gadgetString;
    }
    /**
     * Gets the list of User Preferences.
     * @return - list of user prefs;
     */
    public List<UserPrefDTO> getUserPrefs()
    {
        return userPrefs;
    }

    /**
     * Sets the list of User Preferences.
     * @param inUserPrefs - list of user prefs to assign to the current gadget.
     */
    public void setUserPrefs(final List<UserPrefDTO> inUserPrefs)
    {
        userPrefs = inUserPrefs;
    }

    /**
     * Get the features.
     * @return the features.
     */
    public List<String> getFeatures()
    {
        return features;
    }

    /**
     * Set the features.
     * @param inFeatures the features.
     */
    public void setFeatures(final List<String> inFeatures)
    {
        features = inFeatures;
    }
}
