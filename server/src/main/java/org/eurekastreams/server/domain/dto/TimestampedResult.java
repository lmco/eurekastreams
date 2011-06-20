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
package org.eurekastreams.server.domain.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Container with a timestamp.
 */
public class TimestampedResult<ResultType extends Serializable> implements Serializable
{
    /**
     * The date the result was generated.
     */
    private Date dateGenerated;

    /**
     * The generated result.
     */
    private ResultType result;

    /**
     * @return the dateGenerated
     */
    public Date getDateGenerated()
    {
        return dateGenerated;
    }

    /**
     * Constructor.
     * 
     * @param inResult
     *            the result
     * @param inDateGenerated
     *            the timestamp the result was generated
     */
    public TimestampedResult(final ResultType inResult, final Date inDateGenerated)
    {
        result = inResult;
        dateGenerated = inDateGenerated;
    }

    /**
     * @param inDateGenerated
     *            the dateGenerated to set
     */
    public void setDateGenerated(final Date inDateGenerated)
    {
        dateGenerated = inDateGenerated;
    }

    /**
     * @return the result
     */
    public ResultType getResult()
    {
        return result;
    }

    /**
     * @param inResult
     *            the result to set
     */
    public void setResult(final ResultType inResult)
    {
        result = inResult;
    }

}
