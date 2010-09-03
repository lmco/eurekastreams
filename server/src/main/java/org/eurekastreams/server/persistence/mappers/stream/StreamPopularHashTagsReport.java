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
package org.eurekastreams.server.persistence.mappers.stream;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * List of the popular hashtags for a stream, generated on interval, storing the report date for report expiration.
 */
public class StreamPopularHashTagsReport implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -1745252155558759929L;

    /**
     * Popular hashtags.
     */
    private List<String> popularHashTags;

    /**
     * The date the report was generated.
     */
    private Date reportGenerationDate;

    /**
     * Empty constructor for serialization.
     */
    public StreamPopularHashTagsReport()
    {
    }

    /**
     * Constructor.
     *
     * @param inPopularHashTags
     *            the popular hashtags
     * @param inReportGenerationDate
     *            the date the popular hashtags were generated
     */
    public StreamPopularHashTagsReport(final List<String> inPopularHashTags, final Date inReportGenerationDate)
    {
        popularHashTags = inPopularHashTags;
        reportGenerationDate = inReportGenerationDate;
    }

    /**
     * Get the date this report was generated.
     *
     * @return the reportGenerationDate
     */
    public Date getReportGenerationDate()
    {
        return reportGenerationDate;
    }

    /**
     * Set the date this report was generated.
     *
     * @param inReportGenerationDate
     *            the reportGenerationDate to set
     */
    public void setReportGenerationDate(final Date inReportGenerationDate)
    {
        reportGenerationDate = inReportGenerationDate;
    }

    /**
     * Set the popular hashtags.
     *
     * @param inPopularHashTags
     *            the popularHashTags to set
     */
    public void setPopularHashTags(final List<String> inPopularHashTags)
    {
        popularHashTags = inPopularHashTags;
    }

    /**
     * Get the popular hashtags.
     *
     * @return the popularHashTags
     */
    public List<String> getPopularHashTags()
    {
        return popularHashTags;
    }
}
