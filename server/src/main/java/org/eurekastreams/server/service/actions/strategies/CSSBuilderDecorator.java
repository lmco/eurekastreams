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
package org.eurekastreams.server.service.actions.strategies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Theme;

/**
 * Decorate a Person by converting its XML Theme description to a CSS file.
 */
public class CSSBuilderDecorator extends PersonDecorator
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Force a theme update.
     */
    private boolean forceUpdate;

    /**
     * Holds the XSLT transformation definition.
     */
    private File xsltFile = null;

    /**
     * Fetcher for the XML theme definition.
     */
    private ResourceFetcher xmlFetcher = null;

    /**
     * Destination for the CSS.
     */
    private StreamResult cssResult = null;

    /**
     * Directory name to drop css files in.
     */
    private final String cssFileDirectoryName = "themes";

    /**
     * Time, in milliseconds, that may pass before a CSS file is considered expired.
     * 
     * 1 hour = 3600000
     */
    private long cacheLifetime;

    /**
     * The ServletContext is used to figure out where files belong.
     */
    private ServletContext servletContext = null;

    /**
     * Constructor.
     * 
     * @param next
     *            the next decorator in line
     * @param inXSLTFilename
     *            filename where the XSLT definition can be found
     * @param inXMLFetcher
     *            a Strategy that will provide the source XML
     * @param inCSSResult
     *            the destination for the CSS file
     * @param holder
     *            injecting the ServletContext via this holder
     * @param inCacheLifetime
     *            injecting the cacheLifetime value
     */
    public CSSBuilderDecorator(final PersonDecorator next, final String inXSLTFilename,
            final ResourceFetcher inXMLFetcher, final StreamResult inCSSResult, final ContextHolder holder,
            final Long inCacheLifetime)
    {
        super(next);

        xmlFetcher = inXMLFetcher;
        cssResult = inCSSResult;
        servletContext = holder.getContext();
        xsltFile = new File(servletContext.getRealPath("/") + "/" + inXSLTFilename);
        cacheLifetime = inCacheLifetime.longValue();
        forceUpdate = false;
    }

    /**
     * Convert the XML to CSS - the theme must have already been validated.
     * 
     * @param person
     *            the person to be decorated.
     * @throws IOException
     *             unable to read the XML file
     * @throws TransformerException
     *             transformation from XML to CSS failed
     */
    @Override
    protected void performDecoration(final Person person) throws IOException, TransformerException
    {
        Theme theme = person.getTheme();

        if (null == theme)
        {
            // No theme means no need for a CSS file.
            log.debug("Person has no theme, so no CSS will be made.");
            return;
        }

        // Figure out where on the file system our CSS needs to go.
        File cssFile = getFile(theme.getCssFile());

        if (fileIsFresh(cssFile))
        {
            log.info("CSSBuilderDecorator: using cached CSS file: " + cssFile.getAbsolutePath());
            return;
        }
        log.info("Creating CSS file at " + cssFile.getAbsolutePath());

        cssResult.setOutputStream(new FileOutputStream(cssFile));

        // Load the XSLT
        Source xsltSource = new StreamSource(new FileInputStream(xsltFile));

        TransformerFactory transFactory = TransformerFactory.newInstance();

        javax.xml.transform.Transformer transformer = transFactory.newTransformer(xsltSource);

        // Create the CSS file based on the XML and XSLT
        transformer.transform(new StreamSource(xmlFetcher.getInputStream(theme.getUrl())), cssResult);

    }

    /**
     * Validates a theme by URL.
     * 
     * @param themeUrl
     *            the theme URL.
     * @return boolean true if valid.
     */
    public boolean validateTheme(final String themeUrl)
    {
        boolean themeIsValid = false;

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;

        try
        {
            schema = schemaFactory.newSchema(new StreamSource(servletContext.getRealPath("/") + "/"
                    + "/themes/theme.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFetcher.getInputStream(themeUrl)));

            themeIsValid = true;
        }
        catch (Exception e)
        {
            log.error(e);
        }

        return themeIsValid;
    }

    /**
     * Forces an update on the theme.
     * 
     * @param force
     *            true to force update
     */
    public void setForceUpdate(final boolean force)
    {
        forceUpdate = force;
    }

    /**
     * Check whether this file is recent enough to use as a cache.
     * 
     * @param cssFile
     *            the file to be checked.
     * @return true if the file is new enough, false otherwise
     */
    private boolean fileIsFresh(final File cssFile)
    {
        long now = Calendar.getInstance().getTimeInMillis();

        log.debug("Current time: " + now);
        log.debug("Theme Exists: " + cssFile.exists());
        log.debug("File fresh: " + (cssFile.lastModified() + cacheLifetime > now));
        log.debug("Force update: " + forceUpdate);

        return cssFile.exists() && (cssFile.lastModified() + cacheLifetime > now) && !forceUpdate;
    }

    /**
     * Get a File object for the path specified, including creating directories as needed.
     * 
     * @param url
     *            the URl for the CSS file
     * @return a File representing the new CSS file
     * @throws IOException
     *             on error
     */
    private File getFile(final String url) throws IOException
    {
        String root = servletContext.getRealPath(File.separator);
        String path = root + url;

        // make sure the target directory exists
        int lastSlash = path.lastIndexOf('/');
        File directories = new File(path.substring(0, lastSlash));

        // make sure the file is still inside the root path - so that an attacker doesn't try to do a ../../
        String allowablePath = root;

        if (!allowablePath.endsWith(File.separator))
        {
            allowablePath += "/";
        }
        allowablePath += cssFileDirectoryName;

        File allowablePathFile = new File(allowablePath);

        String allowableCanonicalPath = allowablePathFile.getCanonicalPath();

        String canonicalPath = directories.getCanonicalPath();
        if (!canonicalPath.startsWith(allowableCanonicalPath))
        {
            throw new SecurityException("Directory " + canonicalPath + " is outside the allowable directory "
                    + allowablePath);
        }

        directories.mkdirs();
        return new File(path);
    }
}
