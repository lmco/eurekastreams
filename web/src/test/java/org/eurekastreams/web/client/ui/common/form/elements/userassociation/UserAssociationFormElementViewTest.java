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
package org.eurekastreams.web.client.ui.common.form.elements.userassociation;

import junit.framework.Assert;

import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Test the view.
 */
public class UserAssociationFormElementViewTest
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
     * System under test.
     */
    private UserAssociationFormElementView sut;

    /**
     * Mock model.
     */
    private UserAssociationFormElementModel model = context.mock(UserAssociationFormElementModel.class);

    /**
     * Mock item.
     */
    private MembershipCriteriaItemComposite itemMock = context.mock(MembershipCriteriaItemComposite.class);
    
    /**
     * Mock jsni facade.
     */
    private final WidgetJSNIFacadeImpl jsniFacadeMock = context.mock(WidgetJSNIFacadeImpl.class);


    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new UserAssociationFormElementView(model, jsniFacadeMock)
        {
            @Override
            protected MembershipCriteriaItemComposite getItem(final MembershipCriteria inGroup)
            {
                return itemMock;
            }
        };

        sut.accessGroupsPanel = context.mock(FlowPanel.class, "accessGroupsPanel");
        sut.attr = context.mock(RadioButton.class, "attr");
        sut.description = context.mock(Label.class, "description");
        sut.group = context.mock(RadioButton.class, "group");
        sut.membershipCriteria = context.mock(TextBox.class, "membershipCriteria");
        sut.requiredLabel = context.mock(Label.class, "requiredLabel");
        sut.results = context.mock(Label.class, "results");
        sut.verifyButton = context.mock(Anchor.class, "verifyButton");
        sut.verifying = context.mock(Label.class, "verifying");
    }

    /**
     * Tests adding the verify command.
     */
    @Test
    public final void addVerifyCommandTest()
    {
        final Command verifyCommand = context.mock(Command.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifyButton).addClickHandler(with(any(ClickHandler.class)));

                oneOf(sut.membershipCriteria).addKeyPressHandler(with(any(KeyPressHandler.class)));
            }
        });

        sut.addVerifyCommand(verifyCommand);

        context.assertIsSatisfied();
    }

    /**
     * Tests adding the verify command and clicking the verify button.
     */
    @Test
    public final void addVerifyCommandClickButtonTest()
    {
        final Command verifyCommand = context.mock(Command.class);

        final AnonymousClassInterceptor<ClickHandler> veryifyClickInt = new AnonymousClassInterceptor<ClickHandler>();

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifyButton).addClickHandler(with(any(ClickHandler.class)));
                will(veryifyClickInt);

                oneOf(sut.membershipCriteria).addKeyPressHandler(with(any(KeyPressHandler.class)));

                oneOf(verifyCommand).execute();
            }
        });

        sut.addVerifyCommand(verifyCommand);

        veryifyClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests adding the verify command and pressing enter.
     */
    @Test
    public final void addVerifyCommandPressEnterTest()
    {
        final Command verifyCommand = context.mock(Command.class);

        final AnonymousClassInterceptor<KeyPressHandler> keyPressInt = new AnonymousClassInterceptor<KeyPressHandler>();

        final KeyPressEvent event = context.mock(KeyPressEvent.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifyButton).addClickHandler(with(any(ClickHandler.class)));

                oneOf(sut.membershipCriteria).addKeyPressHandler(with(any(KeyPressHandler.class)));
                will(keyPressInt);

                oneOf(event).getCharCode();
                will(returnValue((char) KeyCodes.KEY_ENTER));

                oneOf(verifyCommand).execute();
            }
        });

        sut.addVerifyCommand(verifyCommand);

        keyPressInt.getObject().onKeyPress(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests adding the verify command and pressing key other than enter.
     */
    @Test
    public final void addVerifyCommandPressNonEnterTest()
    {
        final Command verifyCommand = context.mock(Command.class);

        final AnonymousClassInterceptor<KeyPressHandler> keyPressInt = new AnonymousClassInterceptor<KeyPressHandler>();

        final KeyPressEvent event = context.mock(KeyPressEvent.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifyButton).addClickHandler(with(any(ClickHandler.class)));

                oneOf(sut.membershipCriteria).addKeyPressHandler(with(any(KeyPressHandler.class)));
                will(keyPressInt);

                oneOf(event).getCharCode();
                will(returnValue((char) KeyCodes.KEY_BACKSPACE));

                never(verifyCommand).execute();
            }
        });

        sut.addVerifyCommand(verifyCommand);

        keyPressInt.getObject().onKeyPress(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests adding a group click handler.
     */
    @Test
    public final void addGroupClickHandlerTest()
    {
        final ClickHandler handler = context.mock(ClickHandler.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.group).addClickHandler(handler);
            }
        });

        sut.addGroupClickHandler(handler);

        context.assertIsSatisfied();
    }

    /**
     * Tests adding an attribute click handler.
     */
    @Test
    public final void addAttrClickHandlerTest()
    {
        final ClickHandler handler = context.mock(ClickHandler.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.attr).addClickHandler(handler);
            }
        });

        sut.addAttrClickHandler(handler);

        context.assertIsSatisfied();
    }

    /**
     * Test on verify clicked.
     */
    @Test
    public final void onVerifyClickedTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifying).setVisible(true);
                oneOf(sut.verifyButton).setVisible(false);
                oneOf(sut.requiredLabel).setVisible(false);
                oneOf(sut.results).setVisible(false);
            }
        });

        sut.onVerifyClicked();

        context.assertIsSatisfied();
    }

    /**
     * Test on verify failure.
     */
    @Test
    public final void onVerifyFailureTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifying).setVisible(false);
                oneOf(sut.verifyButton).setVisible(true);
                oneOf(sut.requiredLabel).setVisible(true);
                oneOf(sut.results).setVisible(true);

                oneOf(sut.results).setText(
                        "There was an error processing your request. Please check your query and search again.");
            }
        });

        sut.onVerifyFailure();

        context.assertIsSatisfied();
    }

    /**
     * Tests when attribute search is selected.
     */
    @Test
    public final void onAttributeSearchSelectedTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.description).setText(with(any(String.class)));
            }
        });

        sut.onAttributeSearchSelected();

        context.assertIsSatisfied();
    }

    /**
     * Tests when group search is selected.
     */
    @Test
    public final void onGroupSearchSelectedTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.description).setText(with(any(String.class)));
            }
        });

        sut.onGroupSearchSelected();

        context.assertIsSatisfied();
    }

    /**
     * Tests adding an access group.
     */
    @Test
    public final void addMembershipCriteriaTest()
    {
        final MembershipCriteria criteria = new MembershipCriteria();

        context.checking(new Expectations()
        {
            {
                oneOf(sut.accessGroupsPanel).add(itemMock);

                oneOf(itemMock).addDeleteClickHandler(with(any(ClickHandler.class)));
            }
        });

        sut.addMembershipCriteria(criteria);

        context.assertIsSatisfied();
    }

    /**
     * Tests adding an access group then deleting it.
     */
    @Test
    public final void addRemoveMembershipCriteriaTest()
    {
        final MembershipCriteria criteria = new MembershipCriteria();

        final AnonymousClassInterceptor<ClickHandler> deleteHandlerInt = new AnonymousClassInterceptor<ClickHandler>();

        context.checking(new Expectations()
        {
            {
                oneOf(sut.accessGroupsPanel).add(itemMock);

                oneOf(itemMock).addDeleteClickHandler(with(any(ClickHandler.class)));
                will(deleteHandlerInt);

                oneOf(jsniFacadeMock).confirm(with(any(String.class)));
                will(returnValue(true));
                
                oneOf(model).removeMembershipCriteria(criteria);

                oneOf(sut.accessGroupsPanel).remove(itemMock);
            }
        });

        sut.addMembershipCriteria(criteria);

        deleteHandlerInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Membership criteria property test.
     */
    @Test
    public final void getMembershipCriteriaTest()
    {
        final String criteria = "some.group";

        context.checking(new Expectations()
        {
            {
                oneOf(sut.membershipCriteria).getText();
                will(returnValue(criteria));
            }
        });

        Assert.assertEquals(criteria, sut.getMembershipCriteria());

        context.assertIsSatisfied();
    }

    /**
     * Successful verification test.
     */
    @Test
    public final void onVerifySuccessTest()
    {
        final int numberOfResults = 1;

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifying).setVisible(false);
                oneOf(sut.verifyButton).setVisible(true);
                oneOf(sut.requiredLabel).setVisible(true);
                oneOf(sut.results).setVisible(false);
                oneOf(sut.membershipCriteria).setText("");
            }
        });

        sut.onVerifySuccess(numberOfResults);

        context.assertIsSatisfied();
    }
    
    /**
     * Successful verification test.
     */
    @Test
    public final void onVerifySuccessZeroOrManyPeopleTest()
    {
        final int numberOfResults = 3;

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifying).setVisible(false);
                oneOf(sut.verifyButton).setVisible(true);
                oneOf(sut.requiredLabel).setVisible(true);
                oneOf(sut.results).setVisible(false);
                oneOf(sut.membershipCriteria).setText("");
            }
        });

        sut.onVerifySuccess(numberOfResults);

        context.assertIsSatisfied();
    }
    

    /**
     * Successful verification test with the max results.
     */
    @Test
    public final void onVerifySuccessMaxResultsTest()
    {
        final int numberOfResults = 1000;

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifying).setVisible(false);
                oneOf(sut.verifyButton).setVisible(true);
                oneOf(sut.requiredLabel).setVisible(true);
                oneOf(sut.results).setVisible(false);
                oneOf(sut.membershipCriteria).setText("");
            }
        });

        sut.onVerifySuccess(numberOfResults);

        context.assertIsSatisfied();
    }

    /**
     * Successful verification test with zero results.
     */
    @Test
    public final void onVerifySuccessZeroResultsTest()
    {
        final int numberOfResults = 0;

        context.checking(new Expectations()
        {
            {
                oneOf(sut.verifying).setVisible(false);
                oneOf(sut.verifyButton).setVisible(true);
                oneOf(sut.requiredLabel).setVisible(true);
                oneOf(sut.results).setVisible(false);
                oneOf(sut.membershipCriteria).setText("");
            }
        });

        sut.onVerifySuccess(numberOfResults);

        context.assertIsSatisfied();
    }

    /**
     * Group selected property test.
     */
    @Test
    public final void isGroupSelectedTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.group).getValue();
                will(returnValue(Boolean.TRUE));
            }
        });

        Assert.assertEquals(Boolean.TRUE, (Boolean) sut.isGroupSelected());

        context.assertIsSatisfied();
    }
}
