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
package org.eurekastreams.server.support.email;

/**
 * Specifies information about a email to be built.
 */
public class EmailTemplate
{
    /** Actual template for the subject. */
    private String subjectTemplate;

    /** Resource path for the text form of the body. */
    private String textBodyTemplateResourcePath;

    /** Resource path for the HTML form of the body. */
    private String htmlBodyTemplateResourcePath;

    // ---- short setters for use by Spring p-namespace ----

    /**
     * @param inSubjectTemplate
     *            the subjectTemplate to set
     */
    public void setSubject(final String inSubjectTemplate)
    {
        subjectTemplate = inSubjectTemplate;
    }

    /**
     * @param inTextBodyTemplateResourcePath
     *            the textBodyTemplateResourcePath to set
     */
    public void setTextBody(final String inTextBodyTemplateResourcePath)
    {
        textBodyTemplateResourcePath = inTextBodyTemplateResourcePath;
    }

    /**
     * @param inHtmlBodyTemplateResourcePath
     *            the htmlBodyTemplateResourcePath to set
     */
    public void setHtmlBody(final String inHtmlBodyTemplateResourcePath)
    {
        htmlBodyTemplateResourcePath = inHtmlBodyTemplateResourcePath;
    }

    // ---- normal getters and setters ----

    /**
     * @return the subjectTemplate
     */
    public String getSubjectTemplate()
    {
        return subjectTemplate;
    }

    /**
     * @param inSubjectTemplate
     *            the subjectTemplate to set
     */
    public void setSubjectTemplate(final String inSubjectTemplate)
    {
        subjectTemplate = inSubjectTemplate;
    }

    /**
     * @return the textBodyTemplateResourcePath
     */
    public String getTextBodyTemplateResourcePath()
    {
        return textBodyTemplateResourcePath;
    }

    /**
     * @param inTextBodyTemplateResourcePath
     *            the textBodyTemplateResourcePath to set
     */
    public void setTextBodyTemplateResourcePath(final String inTextBodyTemplateResourcePath)
    {
        textBodyTemplateResourcePath = inTextBodyTemplateResourcePath;
    }

    /**
     * @return the htmlBodyTemplateResourcePath
     */
    public String getHtmlBodyTemplateResourcePath()
    {
        return htmlBodyTemplateResourcePath;
    }

    /**
     * @param inHtmlBodyTemplateResourcePath
     *            the htmlBodyTemplateResourcePath to set
     */
    public void setHtmlBodyTemplateResourcePath(final String inHtmlBodyTemplateResourcePath)
    {
        htmlBodyTemplateResourcePath = inHtmlBodyTemplateResourcePath;
    }
}