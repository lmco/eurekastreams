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

import static org.eurekastreams.web.client.CommandActionProcessorMockSupport.setupActionProcessor;

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.AvatarEntity;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Test for model.
 *
 */
public class ImageCropContentModelTest
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
     * The tab for this layout test.
     */
    private final List<Integer> coords = new LinkedList<Integer>();
    /**
     * Mock action processor.
     */
    final ActionProcessor actionProcessorMock = context
            .mock(ActionProcessor.class);

    /**
     * Execution sequence.
     */
    final Sequence executeSequence = context.sequence("executeSequence");

    /**
     * The view mock.
     */
    final ImageCropContentView viewMock = context.mock(ImageCropContentView.class);

    /**
     * sut.
     */
    private ImageCropContentModel sut = new ImageCropContentModel(actionProcessorMock);

    /**
     * Setup test fixture.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        sut.registerView(viewMock);
    }
    
    /**
     * Test.
     */
    @Test
    public void setCoordsOnSuccess()
    {
        final AvatarEntity result = context.mock(AvatarEntity.class);
            
        final AsyncCallback<AvatarEntity> commandCallback = setupActionProcessor(
                context, actionProcessorMock, executeSequence);
        
        coords.add(new Integer(1));
        coords.add(new Integer(2));
        coords.add(new Integer(3));
        
        context.checking(new Expectations()
        {
            {
                atLeast(1).of(viewMock).getStrategy();

                oneOf(viewMock).onStrategyUpdated(with(any(ImageUploadStrategy.class)));
                oneOf(viewMock).onIsShownChanged(false);
            }
        });
        
        sut.setCoords(coords);
        commandCallback.onSuccess(result);
        context.assertIsSatisfied();
    }
    
    /**
     * Test.
     */
    @Test
    public void setCoordsOnFailure()
    {
            
        final AsyncCallback<AvatarEntity> commandCallback = setupActionProcessor(
                context, actionProcessorMock, executeSequence);
        coords.add(new Integer(1));
        coords.add(new Integer(2));
        coords.add(new Integer(3));
        
        context.checking(new Expectations()
        {
            {
                atLeast(1).of(viewMock).getStrategy();
            }
        });
        
        sut.setCoords(coords);
        commandCallback.onFailure(new Throwable(""));
        context.assertIsSatisfied();
    }

}
