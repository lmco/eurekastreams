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
package org.eurekastreams.server.action.request.gallery;

import java.io.Serializable;

/**
 * Request to "compress" the gadget order in a tab.
 */
public class CompressGadgetZoneRequest implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = 2760200715811279695L;

    /** ID of template to compress. */
    private Long tabTemplateId;

    /** ID of template's owner (may be null for shared templates). */
    private Long tabTemplateOwnerId;

    /** Gadget zone to compress. */
    private Integer zoneNumber;

    /**
     * Constructor.
     *
     * @param inTabTemplateId
     *            ID of template to compress.
     * @param inZoneNumber
     *            Gadget zone to compress.
     * @param inTabTemplateOwnerId
     *            ID of template's owner.
     */
    public CompressGadgetZoneRequest(final Long inTabTemplateId, final Integer inZoneNumber,
            final Long inTabTemplateOwnerId)
    {
        tabTemplateId = inTabTemplateId;
        zoneNumber = inZoneNumber;
        tabTemplateOwnerId = inTabTemplateOwnerId;
    }

    /**
     * @return the tabTemplateId
     */
    public Long getTabTemplateId()
    {
        return tabTemplateId;
    }

    /**
     * @param inTabTemplateId
     *            the tabTemplateId to set
     */
    public void setTabTemplateId(final Long inTabTemplateId)
    {
        tabTemplateId = inTabTemplateId;
    }

    /**
     * @return the tabTemplateOwnerId
     */
    public Long getTabTemplateOwnerId()
    {
        return tabTemplateOwnerId;
    }

    /**
     * @param inTabTemplateOwnerId
     *            the tabTemplateOwnerId to set
     */
    public void setTabTemplateOwnerId(final Long inTabTemplateOwnerId)
    {
        tabTemplateOwnerId = inTabTemplateOwnerId;
    }

    /**
     * @return the zoneNumber
     */
    public Integer getZoneNumber()
    {
        return zoneNumber;
    }

    /**
     * @param inZoneNumber
     *            the zoneNumber to set
     */
    public void setZoneNumber(final Integer inZoneNumber)
    {
        zoneNumber = inZoneNumber;
    }
}
