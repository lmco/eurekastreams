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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.validator.Length;
import org.hibernate.validator.Min;

/**
 * This class represents the System Settings domain object.
 *
 */
@SuppressWarnings("serial")
@Entity
public class SystemSettings extends DomainEntity implements Serializable
{
    /**
     * Max characters.
     */
    @Transient
    public static final int MAX_INPUT = 200;

    /**
     * Minimum number of days for the Terms of Service Prompt Interval.
     */
    @Transient
    public static final int MIN_TOS_PROMPT_INTERVAL = 1;
    
    /**
     * Minimum number of days for Content Expiration.
     */
    @Transient
    public static final int MIN_CONTENT_EXPIRATION = 0;
    
    /**
     * Maximum number of days for Content Expiration.
     */
    @Transient
    public static final int MAX_CONTENT_EXPIRATION = 365;

    /**
     * Max Support Phone Number Characters.
     */
    @Transient
    public static final int MAX_SUPPORT_PHONE_NUMBER_LENGTH = 50;
    
    /**
     * Max Support Email Address Characters. 
     */
    @Transient
    public static final int MAX_SUPPORT_EMAIL_ADDRESS_LENGTH = 50;

    /**
     * Storage for the siteLabel.
     */
    @Basic(optional = true)
    @Length(max = MAX_INPUT)
    private String siteLabel;

    /**
     * TermsOfService associated with the theme as a string.
     */
    @Basic(optional = true)
    private String termsOfService;

    /**
     * The terms of service prompt interval (in days).
     */
    @Basic(optional = true)
    @Min(value = MIN_TOS_PROMPT_INTERVAL)
    private Integer tosPromptInterval = null;

    /**
     * The content warning text.
     */
    @Basic(optional = true)
    @Length(max = MAX_INPUT)
    private String contentWarningText;

    /**
     * the number of days after which stream activities will be deleted.
     */
    @Basic(optional = false)
    @Min(value = MIN_CONTENT_EXPIRATION)
    private Integer contentExpiration = null;

    /**
     * the list of ldap groups.
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "systemSettingsId")
    private List<MembershipCriteria> membershipCriteria = new ArrayList<MembershipCriteria>();

    /**
     * If welcome emails should be sent.
     */
    @Basic(optional = false)
    private Boolean sendWelcomeEmails;
    
    /**
     * Waring message for Plugins.
     */
    private String pluginWarning;    

    /**
     * The short name of the group stream that provides help to users.
     */
    private String supportStreamGroupShortName;

    /**
     * The display name of the support group stream.
     */
    @Transient
    private String supportStreamGroupDisplayName; 

    /**
     * The stream support phone number.
     */
    @Length(max = MAX_SUPPORT_PHONE_NUMBER_LENGTH)
    private String supportPhoneNumber;

    /**
     * The stream support email address.
     */
    @Length(max = MAX_SUPPORT_EMAIL_ADDRESS_LENGTH)
    private String supportEmailAddress;

    /**
     * @return Returns the site label.
     */
    public String getSiteLabel()
    {
        return siteLabel;
    }

    /**
     * @param inSiteLabel
     *            The site label.
     */
    public void setSiteLabel(final String inSiteLabel)
    {
        this.siteLabel = inSiteLabel;
    }

    /**
     * @return the terms of service.
     */
    public String getTermsOfService()
    {
        return termsOfService;
    }

    /**
     * @param inTermsOfService
     *            TermsOfService to use.
     */
    public void setTermsOfService(final String inTermsOfService)
    {
        this.termsOfService = inTermsOfService;
    }

    /**
     * @return the terms of service prompt interval
     */
    public int getTosPromptInterval()
    {
        return tosPromptInterval;
    }

    /**
     * @param inTosPromptInterval
     *            the terms of service prompt interval to set
     */
    public void setTosPromptInterval(final int inTosPromptInterval)
    {
        this.tosPromptInterval = inTosPromptInterval;
    }

    /**
     * @param inContentWarningText
     *            the ContentWarningText to set
     */
    public void setContentWarningText(final String inContentWarningText)
    {
        this.contentWarningText = inContentWarningText;
    }

    /**
     * @return the content expiration
     */
    public int getContentExpiration()
    {
        return contentExpiration;
    }

    /**
     * @param inContentExpiration
     *            the contentExpiration to set
     */
    public void setContentExpiration(final int inContentExpiration)
    {
        this.contentExpiration = inContentExpiration;
    }

    /**
     * @return the content warning text
     */
    public String getContentWarningText()
    {
        return contentWarningText;
    }

    /**
     * @param inMembershipCriteria
     *            the membership criteria to set.
     */
    public void setMembershipCriteria(final List<MembershipCriteria> inMembershipCriteria)
    {
        this.membershipCriteria = inMembershipCriteria;
    }

    /**
     * @return the membership criteria.
     */
    public List<MembershipCriteria> getMembershipCriteria()
    {
        return membershipCriteria;
    }

    /**
     * @param inSendWelcomeEmails
     *            the sendWelcomeEmails to set
     */
    public void setSendWelcomeEmails(final Boolean inSendWelcomeEmails)
    {
        this.sendWelcomeEmails = inSendWelcomeEmails;
    }

    /**
     * @return the sendWelcomeEmails
     */
    public Boolean getSendWelcomeEmails()
    {
        return sendWelcomeEmails;
    }

    /**
     * 
     * @param inPluginWarning
     * the plugin warning setter.
     */
    public void setPluginWarning(final String inPluginWarning)
    {
        this.pluginWarning = inPluginWarning;
    }
    
    /**
     * 
     * @return the plugin warning text.
     */
    public String getPluginWarning()
    {
        return pluginWarning;
    }

    /**
     * Get the phone number for system support.
     *
     * @return the phone number for system support
     */
    public String getSupportPhoneNumber()
    {
        return supportPhoneNumber;
    }

    /**
     * Set the phone number for system support.
     *
     * @param inSupportPhoneNumber
     *            the phone number for system support
     */
    public void setSupportPhoneNumber(final String inSupportPhoneNumber)
    {
        supportPhoneNumber = inSupportPhoneNumber;
    }

    /**
     * Get the email address for system support.
     *
     * @return the email address for system support
     */
    public String getSupportEmailAddress()
    {
        return supportEmailAddress;
    }

    /**
     * Set the email address for system support.
     *
     * @param inSupportEmailAddress
     *            the email address for system support
     */
    public void setSupportEmailAddress(final String inSupportEmailAddress)
    {
        supportEmailAddress = inSupportEmailAddress;
    }

    /**
     * Get the short name of the group stream that provides help to users.
     *
     * @return the short name of the group stream that provides help to users
     */
    public String getSupportStreamGroupShortName()
    {
        return supportStreamGroupShortName;
    }

    /**
     * Set the short name of the group stream that provides help to users.
     *
     * @param inSupportStreamGroupShortName
     *            the short name of the group stream that provides help to users
     */
    public void setSupportStreamGroupShortName(final String inSupportStreamGroupShortName)
    {
        supportStreamGroupShortName = inSupportStreamGroupShortName;
    }

    /**
     * @param inSupportStreamGroupDisplayName
     *          the supportStreamGroupDisplayName to set
     */
    public void setSupportStreamGroupDisplayName(final String inSupportStreamGroupDisplayName)
    {
        supportStreamGroupDisplayName = inSupportStreamGroupDisplayName;
    }

    /**
     * @return the supportStreamGroupDisplayName
     */
    public String getSupportStreamGroupDisplayName()
    {
        return supportStreamGroupDisplayName;
    }
}
