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
package org.eurekastreams.web.client.ui.common.form.elements.avatar;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Tests for view.
 *
 */
public class AvatarUploadFormElementViewTest
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
     * Mock.
     */
    private final AvatarUploadFormElement widget = context
            .mock(AvatarUploadFormElement.class);

    /**
     * Mock.
     */
    private final AvatarUploadFormElementController controller = context
            .mock(AvatarUploadFormElementController.class);

    /**
     * Mock.
     */
    private final ImageUploadStrategy strategy = context.mock(ImageUploadStrategy.class);

    /**
     * SUT.
     */
    private AvatarUploadFormElementView sut = new AvatarUploadFormElementView(controller, widget, strategy);

    /**
     * Mock.
     */
    private final Anchor editButton = context.mock(Anchor.class, "edit");

    /**
     * Mock.
     */
    private final Anchor deleteButton = context.mock(Anchor.class, "del");

    /**
     * Mock.
     */
    private final FlowPanel errorBox = context.mock(FlowPanel.class);

    /**
     * Mock.
     */
    private final FlowPanel avatarContainer = context.mock(FlowPanel.class, "ac");

    /**
     * Mock.
     */
    private final Image hiddenImage = context.mock(Image.class);

    /** Fixture: person. */
    private Person person = context.mock(Person.class, "person");

    /**
     * Setup test fixture.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        sut.deleteButton = deleteButton;
        sut.editButton = editButton;
        sut.errorBox = errorBox;
        sut.avatarContainer = avatarContainer;
        sut.hiddenImage = hiddenImage;
    }

    /**
     * Cleanup after each test.
     */
    @After
    public void tearDown()
    {
        // clear the current person after each test to leave as it would have started
        Session.getInstance().setCurrentPerson(null);
    }

    /**
     * Test.
     */
    @Test
    public void init()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(controller).addFormHandler(with(any(FormPanel.class)));
                oneOf(controller).addResizeClickListener(editButton);
                oneOf(controller).addHiddenImageLoadListener(with(any(Image.class)),
                        with(any(AvatarUploadFormElementView.class)));
            }
        });


        sut.init();
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testShowResizeModal()
    {
        final Dialog dialog = context.mock(Dialog.class);

        context.checking(new Expectations()
        {
            {
                oneOf(widget).createImageCropContent(with(any(ImageUploadStrategy.class)),
                        with(any(String.class)), with(any(String.class)), with(any(String.class)));
                oneOf(widget).createDialog(with(any(DialogContent.class)));
                will(returnValue(dialog));
                oneOf(dialog).setBgVisible(true);
                oneOf(dialog).center();
            }
        });

        sut.showResizeModal("5px", "5px");
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void onResizePanelShownChanged()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(strategy).getId();
                will(returnValue(5L));

                oneOf(hiddenImage).setUrl(with(any(String.class)));
            }
        });

        sut.onResizePanelShownChanged(true);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void onFormResultChangedWithSuccess()
    {
        Session.getInstance().setCurrentPerson(person);

        context.checking(new Expectations()
        {
            {
                oneOf(errorBox).setVisible(false);
                oneOf(strategy).setX(1);
                oneOf(strategy).setY(2);
                oneOf(strategy).setCropSize(3);

                oneOf(widget).createImage(strategy, "something");
                oneOf(avatarContainer).clear();
                oneOf(avatarContainer).add(with(any(Image.class)));
                oneOf(editButton).setVisible(true);
                oneOf(deleteButton).setVisible(true);

                oneOf(person).setAvatarId("something");
            }
        });


        sut.onFormResultChanged("something,1,2,3");
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void onFormResultChangedWithFail()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(errorBox).setVisible(true);
            }
        });


        sut.onFormResultChanged("fail");
        context.assertIsSatisfied();
    }


}
