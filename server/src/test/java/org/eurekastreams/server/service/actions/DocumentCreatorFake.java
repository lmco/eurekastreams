/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 * Document creator, used by AddThemeAction.
 */
public class DocumentCreatorFake
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(DocumentCreatorFake.class);

    /**
     * Returns a dummy document.
     * 
     * @param inFileName
     *            the name of the file
     * @return an xml document.
     * @throws Exception
     *             Thrown when cannot create the xml document.
     */
    public Document execute(final String inFileName) throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document xmlDoc = db.newDocument();
        
        xmlDoc.createElement("Title");
        xmlDoc.setNodeValue("theme title");
        
        xmlDoc.createElement("Description");
        xmlDoc.setNodeValue("theme description");
        
        xmlDoc.createElement("AuthorName");
        xmlDoc.setNodeValue("author name");
        
        xmlDoc.createElement("AuthorEmail");
        xmlDoc.setNodeValue("author email");
        
        return xmlDoc;
        
    }
}
