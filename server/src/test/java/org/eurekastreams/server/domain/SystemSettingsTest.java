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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.eurekastreams.server.domain.dto.ThemeDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for SystemSettings.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class SystemSettingsTest
{
    /**
     * test SiteLabel.
     */
    private final String testSiteLabel = "some site label";

    /**
     * test terms of service.
     */
    private final String testTermsOfService = "some terms of service";

    /**
     * test terms of service prompt interval.
     */
    private final int testTosPromptInterval = 1;

    /**
     * test content warning.
     */
    private final String testContentWarning = "some content warning";

    /**
     * test content expiration.
     */
    private final int testContentExpiration = 1;

    /**
     * Test send welcome emails.
     */
    private final Boolean sendWelcomeEmails = Boolean.TRUE;

    /**
     * The stream that provides help to users.
     */
    private String supportStreamGroupShortName = "sdlkjfsdlk";

    /**
     * The stream that provides help to users.
     */
    private String supportStreamGroupDisplayName = "Sdlkjfsdlk Ajkldfjlkfd";

    /**
     * The stream support phone number.
     */
    private String supportPhoneNumber = "sdlkjfsdlkjdf";

    /**
     * The stream support email address.
     */
    private String supportEmailAddress = "sldkfjsdlkjfsdjfklsdddd";

    /**
     * test membership criteria.
     */
    private final List<MembershipCriteriaDTO> testMembershipCriteria = new ArrayList<MembershipCriteriaDTO>();

    /**
     * test gallery tab templates.
     */
    private final List<GalleryTabTemplateDTO> testGalleryTabTemplates = new ArrayList<GalleryTabTemplateDTO>();

    /**
     * test themes.
     */
    private final List<ThemeDTO> testThemes = new ArrayList<ThemeDTO>();

    /**
     * Subject under test.
     */
    private SystemSettings systemSettings;

    /**
     * Test class setup.
     */
    @Before
    public final void setUp()
    {
        systemSettings = new SystemSettings();
        systemSettings.setSiteLabel(testSiteLabel);
        systemSettings.setTermsOfService(testTermsOfService);
        systemSettings.setTosPromptInterval(testTosPromptInterval);
        systemSettings.setContentWarningText(testContentWarning);
        systemSettings.setContentExpiration(testContentExpiration);
        systemSettings.setMembershipCriteria(testMembershipCriteria);
        systemSettings.setSendWelcomeEmails(sendWelcomeEmails);
        systemSettings.setThemes(testThemes);
        systemSettings.setGalleryTabTemplates(testGalleryTabTemplates);
    }
    
    /**
     * Test SiteLabel to setter/getter.
     */
    @Test
    public void setAndGetSiteLabel()
    {
        systemSettings.setSiteLabel(testSiteLabel);
        assertEquals("property should be gotten", testSiteLabel, systemSettings.getSiteLabel());
    }

    /**
     * Test sendWelcomeEmails to setter/getter.
     */
    @Test
    public void setAndGetSendWelcomeEmails()
    {
        systemSettings.setSendWelcomeEmails(sendWelcomeEmails);
        assertEquals("property should be gotten", sendWelcomeEmails, systemSettings.getSendWelcomeEmails());
    }

    /**
     * Test TermsOfService setter/getter.
     */
    @Test
    public void setAndGetTermsOfService()
    {
        assertEquals("property should be gotten", testTermsOfService, systemSettings.getTermsOfService());
    }

    /**
     * Test TosPromptInterval setter/getter.
     */
    @Test
    public void setAndGetTosPromptInterval()
    {
        assertEquals("property should be gotten", testTosPromptInterval, systemSettings.getTosPromptInterval());
    }

    /**
     * Test content warning setter/getter.
     */
    @Test
    public void setAndGetContentWarning()
    {
        assertEquals("property should be gotten", testContentWarning, systemSettings.getContentWarningText());
    }

    /**
     * Test content expiration setter/getter.
     */
    @Test
    public void setAndGetContentExpiration()
    {
        assertEquals("property should be gotten", testContentExpiration, systemSettings.getContentExpiration());
    }

    /**
     * Test content ldap groups setter/getter.
     */
    @Test
    public void setAndGetLdapGroups()
    {
        assertEquals("property should be gotten", testMembershipCriteria, systemSettings.getMembershipCriteria());
    }

    /**
     * Test themes setter/getter.
     */
    @Test
    public void setAndGetThemes()
    {
        assertEquals("property should be gotten", testThemes, systemSettings.getThemes());
    }

    /**
     * Test gallerytabtemplate setter/getter.
     */
    @Test
    public void setAndGetGalleryTabTemplate()
    {
        assertEquals("property should be gotten", testGalleryTabTemplates, systemSettings.getGalleryTabTemplates());
    }

}
