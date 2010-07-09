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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertEquals;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * 
 * TutorialVideo Test class..
 *
 */
public class TutorialVideoTest
{
    /**
     * Context for building mock objects.
     */
    @SuppressWarnings("unused")
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Text fixture.
     */
    private TutorialVideo sut;
    
    /**
     * Dialog video title.
     */
    private String dialogTitle = "Dialog Title";

    /**
     * Video content title.
     */
    private String innerContentTitle = "Content Title";
    
    /**
     * Video Content.
     */
    private String innerContent = "Inner test content";
    
    /**
     * Video URL.
     */
    private String videoUrl = "http://google.com";
    /**
     * Video width.
     */
    private final Integer videoWidth = 200;

    /**
     * Video height.
     */
    private final Integer videoHeight = 600;
    
    /**
     * Test setters/getters.
     */
    @Test
    public void testSetTutorialVideo()
    {
        sut = new TutorialVideo();
        sut.setDialogTitle(dialogTitle);
        sut.setInnerContent(innerContent);
        sut.setInnerContentTitle(innerContentTitle);
        sut.setVideoHeight(videoHeight);
        sut.setVideoUrl(videoUrl);
        sut.setVideoWidth(videoWidth);
        assertEquals("Dialog Title", sut.getDialogTitle());
        assertEquals("Content Title", sut.getInnerContentTitle());
        assertEquals("Inner test content", sut.getInnerContent());
        assertEquals("http://google.com", sut.getVideoUrl());
        assertEquals(videoWidth, sut.getVideoWidth());
        assertEquals(videoHeight, sut.getVideoHeight());
        
    }
    
   
}
