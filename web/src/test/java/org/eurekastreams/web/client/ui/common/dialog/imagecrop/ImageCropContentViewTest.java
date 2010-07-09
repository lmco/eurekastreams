/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.dialog.imagecrop;

import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Image Crop content view test.
 */
public class ImageCropContentViewTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };

    /**
     * widget mock.
     */
    private final ImageCropContent widget = context.mock(ImageCropContent.class);
    /**
     * COntroller Mock.
     */
    private final ImageCropContentController controller = context.mock(ImageCropContentController.class);
    /**
     * Command mock.
     */
    private final Command saveCommand = context.mock(Command.class);
    
    /**
     * sut.
     */
    private ImageCropContentView sut = new ImageCropContentView(controller, widget, saveCommand, null);
    
    /**
     * init test.
     */
    @Test
    public void init()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(controller).addCloseClickListener(with(any(Hyperlink.class)));
                oneOf(controller).addSaveClickListener(with(any(Hyperlink.class)), 
                        with(any(ImageCropContentView.class)));
            }
        });

        
        sut.init();
        context.assertIsSatisfied();
    }
    
    /**
     * test getting coordinators.
     */
    @Test
    public void getCoords()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(widget).getCoords();
            }
        });

        
        sut.getCoords();
        context.assertIsSatisfied();
    }
    
    /**
     *  test entity change.
     */
    @Test
    public void onEntityChanged()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(saveCommand).execute();
            }
        });

        
        sut.onStrategyUpdated(null);
        context.assertIsSatisfied();
    }
    
    /**
     *  test shown when changed is true.
     */
    @Test
    public void onIsShownChangedWithTrue()
    {  
        sut.onIsShownChanged(true);
        context.assertIsSatisfied();
    }
    
    /**
     * changed with false.
     */
    @Test
    public void onIsShownChangedWithFalse()
    {  
        context.checking(new Expectations()
        {
            {
                oneOf(widget).close();
            }
        });
        
        sut.onIsShownChanged(false);
        context.assertIsSatisfied();
    }
    
    /**
     * Test getting entity.
     */
    @Test
    public void getEntity()
    {  
        ImageUploadStrategy result = sut.getStrategy();
        Assert.assertEquals(result, null);
    }
}
