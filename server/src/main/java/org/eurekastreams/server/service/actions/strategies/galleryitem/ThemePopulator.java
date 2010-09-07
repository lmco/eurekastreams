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
package org.eurekastreams.server.service.actions.strategies.galleryitem;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.service.actions.strategies.DocumentCreator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates and returns a theme.
 * 
 */
public class ThemePopulator implements GalleryItemPopulator<Theme>
{
    /**
     * Log.
     */
    Log log = LogFactory.make();

    /**
     * Max css file name prefix length, grabbed from the theme name.
     */
    private static final Integer MAX_THEME_CSS_FILE_PREFIX_LENGTH = 20;

    /**
     * Fetcher for the XML theme definition.
     */
    private DocumentCreator documentCreator = null;

    /**
     * Constructor.
     * 
     * @param inDocumentCreator
     *            injecting a DocumentCreator
     */
    public ThemePopulator(final DocumentCreator inDocumentCreator)
    {
        documentCreator = inDocumentCreator;
    }

    /**
     * Populates a theme.
     * 
     * @param inTheme
     *            the theme to populate
     * @param inThemeUrl
     *            the theme url.
     */
    public void populate(final Theme inTheme, final String inThemeUrl)
    {
        Document xmlDoc = null;
        try
        {
            xmlDoc = documentCreator.execute(inThemeUrl);
        }
        catch (Exception e)
        {
            log.error(e);
        }
        xmlDoc.getDocumentElement().normalize();

        NodeList nodeList = xmlDoc.getElementsByTagName("Title");
        Element element = (Element) nodeList.item(0);
        nodeList = element.getChildNodes();
        String themeName = (nodeList.item(0)).getNodeValue();
        inTheme.setName(themeName);

        nodeList = xmlDoc.getElementsByTagName("Description");
        element = (Element) nodeList.item(0);
        nodeList = element.getChildNodes();
        String descrption = (nodeList.item(0)).getNodeValue();
        inTheme.setDescription(descrption);

        nodeList = xmlDoc.getElementsByTagName("AuthorName");
        element = (Element) nodeList.item(0);
        nodeList = element.getChildNodes();
        String authorName = (nodeList.item(0)).getNodeValue();
        inTheme.setAuthorName(authorName);

        nodeList = xmlDoc.getElementsByTagName("AuthorEmail");
        element = (Element) nodeList.item(0);
        nodeList = element.getChildNodes();
        String authorEmail = (nodeList.item(0)).getNodeValue();
        inTheme.setAuthorEmail(authorEmail);

        nodeList = xmlDoc.getElementsByTagName("HeaderBackground");
        Node headerBackgroundNode = nodeList.item(0);
        Element headerBackgroundItemImageElement = (Element) headerBackgroundNode;
        nodeList = headerBackgroundItemImageElement.getElementsByTagName("HeaderBackgroundImage");
        element = (Element) nodeList.item(0);
        nodeList = element.getChildNodes();
        String bannerId = (nodeList.item(0)).getNodeValue();
        inTheme.setBannerId(bannerId);

        String cssFile = "/themes/" + getCleanedThemeName(themeName) + UUID.randomUUID() + ".css";
        inTheme.setCssFile(cssFile);
    }

    /**
     * Clean up the input theme name to be usable as a file name.
     * 
     * @param inThemeName
     *            the theme name
     * @return the cleaned version of the file name - the first 20 characters of non-forward-slash
     */
    private String getCleanedThemeName(final String inThemeName)
    {
        String cleanedName = inThemeName.replace('/', '-');
        if (cleanedName.length() > MAX_THEME_CSS_FILE_PREFIX_LENGTH)
        {
            cleanedName = cleanedName.substring(0, MAX_THEME_CSS_FILE_PREFIX_LENGTH);
        }
        return cleanedName;
    }
}
