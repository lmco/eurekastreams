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
package org.eurekastreams.server.service.actions.strategies.activity.plugins.rome;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.io.ModuleParser;

/**
 * Parser for the ROME module.
 * 
 */
public class ActivityStreamsModuleParser implements ModuleParser
{
    /**
     * Local instance of logger.
     */
    private final Log logger = LogFactory.getLog(ActivityStreamsModuleParser.class);

    /**
     * Gets the namespace URI, needed for ROME module.
     * 
     * @return the namespace.
     */
    public String getNamespaceUri()
    {
        return ActivityStreamsModule.URI;
    }

    /**
     * Parse the element and turn it into a module.
     * 
     * @param root
     *            the root element.
     * @return the parsed module.
     */
    @SuppressWarnings("unchecked")
    public Module parse(final Element root)
    {
        Namespace myNamespace = Namespace.getNamespace(ActivityStreamsModule.URI);
        ActivityStreamsModule myModule = new ActivityStreamsModuleImpl();

        SyndEntryImpl atomEntry = new SyndEntryImpl();

        Element objElement = root.getChild("object", myNamespace);

        List<Element> children = (List<Element>) objElement.getChildren();
        for (Element child : children)
        {
            if (child.getName().equals("title"))
            {
                atomEntry.setTitle(child.getTextTrim());
            }
            else if (child.getName().equals("content"))
            {
                List<SyndContent> contents = new LinkedList<SyndContent>();
                SyndContent content = new SyndContentImpl();
                content.setValue(child.getTextTrim());
                contents.add(content);
                atomEntry.setContents(contents);
            }
            else if (child.getName().equals("link"))
            {
                atomEntry.setLink(child.getAttributeValue("href"));
            }
            else if (child.getName().equals("summary"))
            {
                SyndContent content = new SyndContentImpl();
                content.setValue(child.getTextTrim());
                atomEntry.setDescription(content);
            }
            else if (child.getName().equals("object-type"))
            {
                try
                {
                    String[] objArr = child.getTextTrim().split("/");
                    String objStr = objArr[objArr.length - 1].toUpperCase();
                    BaseObjectType.valueOf(objStr);

                    myModule.setObjectType(objStr);
                }
                catch (IllegalArgumentException ex)
                {
                    logger.debug("ActivityObject not valid");
                    myModule.setObjectType("NOTE");
                }
            }
        }

        myModule.setAtomEntry(atomEntry);
        return myModule;
    }
}
