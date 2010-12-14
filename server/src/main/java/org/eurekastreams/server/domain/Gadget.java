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
package org.eurekastreams.server.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.validator.Length;

/**
 * This class represents an instance of a gadget
 *
 * Note: A unique constraint was considered for (tabId, zoneNumber, zoneIndex),
 * but the ORM might create a transient state when rearranging Gadgets that
 * would violate this constraint.
 */
@SuppressWarnings("serial")
@Entity
public class Gadget extends DomainEntity implements Serializable
{
    /**
     * Used for validation.  This length is an attempt to create some limit over
     * how much can be stored in the user preferences.
     */
    @Transient
    private static final int MAX_GADGETUSERPREF_LENGTH = 100000;

    /**
     * Used for validation.
     */
    @Transient
    private static final String GADGETUSERPREF_MESSAGE =
        "Gadget Settings can be no more than " + MAX_GADGETUSERPREF_LENGTH + " characters";

    /**
     * Private reference back to the parent tab for mapper queries originating
     * with the gadget.
     */
    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabTemplateId")
    private TabTemplate template;

    /**
     * The owner of the gadget.
     */
    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId")
    private Person owner;

    /**
     * This field will maintain a link to the corresponding gadget definition
     * for this gadget instance.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "gadgetDefinitionId")
    private GadgetDefinition gadgetDefinition;

    /**
     * This field contains the user preferences
     * for this gadget instance.
     */
    @Basic
    @Length(max = MAX_GADGETUSERPREF_LENGTH, message = GADGETUSERPREF_MESSAGE)
    private String gadgetUserPref;

    /**
     * The zone number describes which zone the gadget is to be displayed in.
     */
    @Basic(optional = false)
    private int zoneNumber;

    /**
     * The minimized bits tracks whether to display the gadget as minimized or
     * normal.
     */
    @Basic(optional = false)
    private boolean minimized = false;

    /**
     * The maximized bits tracks whether to display the gadget as maximized or
     * normal.
     */
    @Basic(optional = true)
    private Boolean maximized = false;

    /**
     * The zone index describes what order in the zone the gadget will be
     * displayed in.
     */
    @Basic(optional = false)
    private int zoneIndex;

    /**
     * The deleted field is used to track the state of the gadget object.
     */
    @SuppressWarnings("unused")
    @Basic(optional = false)
    private boolean deleted;

    /**
     * This is a timestamp that is used to track when a gadget was deleted.
     * Since the deleted record remains in the db until it expires (logic for
     * cleanup is defined in the mapper), this value needs to track the full
     * date and time of when the tab was deleted so that it can be cleaned up
     * with a minute based expiration.
     */
    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDeleted;

    /**
     * Protected constructor for ORM.
     */
    protected Gadget()
    {
        // Nothing to do here, but hibernate needs a default constructor.
    }

    /**
     * Default constructor responsible for assembling the gadget.
     *
     * @param inGadgetDefinition
     *            Definition of the gadget that describes this instance.
     * @param inZoneNumber
     *            Zone to display this gadget in.
     * @param inZoneIndex
     *            Order in the zone to display this gadget in.
     * @param inOwner
     *            Othe gadget owner.
     * @param inGadgetUserPref
     *            String based json representation of the user prefs for this gadget.
     */
    public Gadget(final GadgetDefinition inGadgetDefinition,
            final int inZoneNumber, final int inZoneIndex, final Person inOwner,
            final String inGadgetUserPref)
    {
        gadgetDefinition = inGadgetDefinition;
        zoneNumber = inZoneNumber;
        zoneIndex = inZoneIndex;
        owner = inOwner;
        gadgetUserPref = inGadgetUserPref;
    }

    /**
     * Default constructor responsible for assembling the gadget.
     *
     * @param inGadgetDefinition
     *            Definition of the gadget that describes this instance.
     * @param inZoneNumber
     *            Zone to display this gadget in.
     * @param inZoneIndex
     *            Order in the zone to display this gadget in.
     * @param inOwner
     *            Othe gadget owner.
     */
    public Gadget(final GadgetDefinition inGadgetDefinition,
            final int inZoneNumber, final int inZoneIndex, final Person inOwner)
    {
        this(inGadgetDefinition, inZoneNumber, inZoneIndex, inOwner, "");
    }

    /**
     * Constructor that creates a gadget based on passed in gadget.
     * @param inGadget Gadget to use as "template".
     */
    public Gadget(final Gadget inGadget)
    {
        gadgetDefinition = inGadget.getGadgetDefinition();
        zoneIndex = inGadget.getZoneIndex();
        zoneNumber = inGadget.getZoneNumber();
        minimized = inGadget.isMinimized();
        owner = inGadget.getOwner();
        gadgetUserPref = inGadget.getGadgetUserPref();
    }

    /**
     * Retrieves the current zone number for this gadget.
     *
     * @return zone number to display this gadget in.
     */
    public int getZoneNumber()
    {
        return zoneNumber;
    }

    /**
     * Sets the current zone number for this gadget.
     *
     * @param inZoneNumber
     *            - the zone number to set this to.
     */
    public void setZoneNumber(final int inZoneNumber)
    {
        zoneNumber = inZoneNumber;
    }

    /**
     * Retrieves the current zone index for this gadget.
     *
     * @return zone index that describes what order to display this Gadget
     *         within the zone
     */
    public int getZoneIndex()
    {
        return zoneIndex;
    }

    /**
     * Set the current zone index for this gadget.
     *
     * @param inZoneIndex
     *            the zone index to set to this Gadget.
     */
    public void setZoneIndex(final int inZoneIndex)
    {
        zoneIndex = inZoneIndex;
    }

    /**
     * Definition for the current gadget.
     *
     * @return instance of Owner that describes this gadget instance.
     */
    public Person getOwner()
    {
        return owner;
    }

    /**
     * Private setting for serialization purposes.
     *
     * @param inOwner
     *            The gadget definition.
     */
    public void setOwner(final Person inOwner)
    {
        owner = inOwner;
    }

    /**
     * Definition for the current gadget.
     *
     * @return instance of GadgetDefinition that describes this gadget instance.
     */
    public GadgetDefinition getGadgetDefinition()
    {
        return gadgetDefinition;
    }

    /**
     * Private setting for serialization purposes.
     *
     * @param inGadgetDefinition
     *            The gadget definition.
     */
    public void setGadgetDefinition(final GadgetDefinition inGadgetDefinition)
    {
        gadgetDefinition = inGadgetDefinition;
    }

    /**
     * User Preferences for gadget.
     * @return - current instance of GadgetUserPref.
     */
    public String getGadgetUserPref()
    {
        return gadgetUserPref;
    }

    /**
     * Set the GadgetUserPref.
     * @param inGadgetUserPref - instance of GadgetUserPref to set.
     */
    public void setGadgetUserPref(final String inGadgetUserPref)
    {
        gadgetUserPref = inGadgetUserPref;
    }

    /**
     * @return the minimized
     */
    public boolean isMinimized()
    {
        return minimized;
    }

    /**
     * Setter for minimized state.
     *
     * @param inMinimized
     *            the minimized to set
     */
    public void setMinimized(final boolean inMinimized)
    {
        minimized = inMinimized;
    }

    /**
     * @return the maximized
     */
    public Boolean isMaximized()
    {
        return maximized;
    }

    /**
     * Setter for maximized state.
     *
     * @param inMaximized
     *            the maximized to set
     */
    public void setMaximized(final Boolean inMaximized)
    {
        maximized = inMaximized;
    }

    /**
     * Sets the id.
     * @param inId the id.
     */
    @Override
    public void setId(final long inId)
    {
        super.setId(inId);
    }

}
