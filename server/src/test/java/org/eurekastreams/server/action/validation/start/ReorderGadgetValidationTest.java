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
package org.eurekastreams.server.action.validation.start;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.start.ReorderGadgetRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link ReorderGadgetValidation} strategy.
 *
 */
public class ReorderGadgetValidationTest
{
    /**
     * System under test.
     */
    private ReorderGadgetValidation sut;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Tab mapper mock instance.
     */
    private final TabMapper tabMapperMock = context.mock(TabMapper.class);

    /**
     * Mocked tab for this test suite.
     */
    private final Tab tabMock = context.mock(Tab.class);

    /**
     * Mocked source {@link TabTemplate} for this test suite.
     */
    private final TabTemplate sourceTabTemplateMock = context.mock(TabTemplate.class, "source");

    /**
     * Mocked destination {@link TabTemplate} for this test suite.
     */
    private final TabTemplate destinationTabTemplateMock = context.mock(TabTemplate.class, "destination");

    /**
     * Mocked {@link ServiceActionContext} for this test suite.
     */
    private final ServiceActionContext mockedContext = context.mock(ServiceActionContext.class);

    /**
     * Mocked {@link ReorderGadgetRequest} for this test suite.
     */
    private final ReorderGadgetRequest requestContext = context.mock(ReorderGadgetRequest.class);

    /**
     * Mocked {@link Gadget} instance for this test suite.
     */
    private final Gadget testGadgetOne = context.mock(Gadget.class, "gadgetOne");

    /**
     * Mocked {@link Gadget} instance for this test suite.
     */
    private final Gadget testGadgetTwo = context.mock(Gadget.class, "gadgetTwo");

    /**
     * Mocked {@link Gadget} instance for this test suite.
     */
    private final Gadget testGadgetThree = context.mock(Gadget.class, "gadgetThree");

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new ReorderGadgetValidation(tabMapperMock);
    }

    /**
     * Test a successful run through the Validation.
     */
    @Test
    public void testValidate()
    {
        final List<Gadget> gadgetsList = new ArrayList<Gadget>();
        gadgetsList.add(testGadgetOne);
        gadgetsList.add(testGadgetTwo);
        gadgetsList.add(testGadgetThree);
        final Layout currentLayout = Layout.TWOCOLUMN;

        setupGadgetExpectations();

        context.checking(new Expectations()
        {
            {
                oneOf(mockedContext).getParams();
                will(returnValue(requestContext));

                oneOf(requestContext).getCurrentTabId();
                will(returnValue(1L));

                oneOf(requestContext).getGadgetId();
                will(returnValue(2L));

                oneOf(requestContext).getTargetZoneIndex();
                will(returnValue(0));

                oneOf(requestContext).getTargetZoneNumber();
                will(returnValue(1));

                oneOf(tabMapperMock).findByGadgetId(2L);
                will(returnValue(sourceTabTemplateMock));

                oneOf(tabMapperMock).findById(1L);
                will(returnValue(tabMock));

                oneOf(tabMock).getTemplate();
                will(returnValue(destinationTabTemplateMock));

                oneOf(destinationTabTemplateMock).getTabLayout();
                will(returnValue(currentLayout));

                allowing(mockedContext).getState();

                oneOf(destinationTabTemplateMock).getGadgets();
                will(returnValue(gadgetsList));
            }
        });

        sut.validate(mockedContext);

        context.assertIsSatisfied();
    }

    /**
     * Test handling an invalid Gadget Zone Target.
     */
    @Test
    public void testInvalidTargetZone()
    {
        final List<Gadget> gadgetsList = new ArrayList<Gadget>();
        gadgetsList.add(testGadgetOne);
        gadgetsList.add(testGadgetTwo);
        gadgetsList.add(testGadgetThree);
        final Layout currentLayout = Layout.TWOCOLUMN;

        setupGadgetExpectations();

        context.checking(new Expectations()
        {
            {
                oneOf(mockedContext).getParams();
                will(returnValue(requestContext));

                oneOf(requestContext).getCurrentTabId();
                will(returnValue(1L));

                oneOf(requestContext).getGadgetId();
                will(returnValue(2L));

                oneOf(requestContext).getTargetZoneIndex();
                will(returnValue(2));

                oneOf(requestContext).getTargetZoneNumber();
                will(returnValue(4));

                oneOf(tabMapperMock).findByGadgetId(2L);
                will(returnValue(sourceTabTemplateMock));

                oneOf(tabMapperMock).findById(1L);
                will(returnValue(tabMock));

                oneOf(tabMock).getTemplate();
                will(returnValue(destinationTabTemplateMock));

                oneOf(destinationTabTemplateMock).getTabLayout();
                will(returnValue(currentLayout));

                allowing(mockedContext).getState();
            }
        });

        boolean exceptionWasThrown = false;
        try
        {
            sut.validate(mockedContext);
        }
        catch (ValidationException vex)
        {
            exceptionWasThrown = true;
            assertEquals(1, vex.getErrors().size());
            assertTrue(vex.getErrors().containsKey("invalidZone"));
        }

        assertTrue(exceptionWasThrown);

        context.assertIsSatisfied();
    }

    /**
     * Test handling an invalid gadget id.
     */
    @Test
    public void testInvalidGadgetId()
    {
        final List<Gadget> gadgetsList = new ArrayList<Gadget>();
        gadgetsList.add(testGadgetOne);
        gadgetsList.add(testGadgetTwo);
        gadgetsList.add(testGadgetThree);
        final Layout currentLayout = Layout.TWOCOLUMN;

        setupGadgetExpectations();

        context.checking(new Expectations()
        {
            {
                oneOf(mockedContext).getParams();
                will(returnValue(requestContext));

                oneOf(requestContext).getCurrentTabId();
                will(returnValue(1L));

                oneOf(requestContext).getGadgetId();
                will(returnValue(4L));

                oneOf(requestContext).getTargetZoneIndex();
                will(returnValue(2));

                oneOf(requestContext).getTargetZoneNumber();
                will(returnValue(1));

                oneOf(tabMapperMock).findByGadgetId(4L);
                will(returnValue(null));

                oneOf(tabMapperMock).findById(1L);
                will(returnValue(tabMock));

                oneOf(tabMock).getTemplate();
                will(returnValue(destinationTabTemplateMock));

                oneOf(destinationTabTemplateMock).getTabLayout();
                will(returnValue(currentLayout));

                allowing(mockedContext).getState();
            }
        });

        boolean exceptionWasThrown = false;
        try
        {
            sut.validate(mockedContext);
        }
        catch (ValidationException vex)
        {
            exceptionWasThrown = true;
            assertEquals(1, vex.getErrors().size());
            assertTrue(vex.getErrors().containsKey("invalidGadget"));
        }

        assertTrue(exceptionWasThrown);

        context.assertIsSatisfied();
    }

    /**
     * Test handling an invalid gadget zone index.
     */
    @Test
    public void testInvalidGadgetZoneIndex()
    {
        final List<Gadget> gadgetsList = new ArrayList<Gadget>();
        gadgetsList.add(testGadgetOne);
        gadgetsList.add(testGadgetTwo);
        gadgetsList.add(testGadgetThree);
        final Layout currentLayout = Layout.TWOCOLUMN;

        setupGadgetExpectations();

        context.checking(new Expectations()
        {
            {
                oneOf(mockedContext).getParams();
                will(returnValue(requestContext));

                oneOf(requestContext).getCurrentTabId();
                will(returnValue(1L));

                oneOf(requestContext).getGadgetId();
                will(returnValue(2L));

                oneOf(requestContext).getTargetZoneIndex();
                will(returnValue(4));

                oneOf(requestContext).getTargetZoneNumber();
                will(returnValue(1));

                oneOf(tabMapperMock).findByGadgetId(2L);
                will(returnValue(sourceTabTemplateMock));

                oneOf(tabMapperMock).findById(1L);
                will(returnValue(tabMock));

                oneOf(tabMock).getTemplate();
                will(returnValue(destinationTabTemplateMock));

                oneOf(destinationTabTemplateMock).getTabLayout();
                will(returnValue(currentLayout));

                allowing(mockedContext).getState();

                oneOf(destinationTabTemplateMock).getGadgets();
                will(returnValue(gadgetsList));
            }
        });

        boolean exceptionWasThrown = false;
        try
        {
            sut.validate(mockedContext);
        }
        catch (ValidationException vex)
        {
            exceptionWasThrown = true;
            assertEquals(1, vex.getErrors().size());
            assertTrue(vex.getErrors().containsKey("invalidZoneIndex"));
        }

        assertTrue(exceptionWasThrown);

        context.assertIsSatisfied();
    }

    /**
     * Test handling an invalid gadget zone index.
     */
    @Test
    public void testInvalidDestinationTab()
    {
        final List<Gadget> gadgetsList = new ArrayList<Gadget>();
        gadgetsList.add(testGadgetOne);
        gadgetsList.add(testGadgetTwo);
        gadgetsList.add(testGadgetThree);
        final Layout currentLayout = Layout.TWOCOLUMN;

        context.checking(new Expectations()
        {
            {
                oneOf(mockedContext).getParams();
                will(returnValue(requestContext));

                oneOf(requestContext).getCurrentTabId();
                will(returnValue(1L));

                oneOf(requestContext).getGadgetId();
                will(returnValue(2L));

                oneOf(requestContext).getTargetZoneIndex();
                will(returnValue(4));

                oneOf(requestContext).getTargetZoneNumber();
                will(returnValue(1));

                oneOf(tabMapperMock).findByGadgetId(2L);
                will(returnValue(sourceTabTemplateMock));

                oneOf(tabMapperMock).findById(1L);
                will(returnValue(null));
            }
        });

        boolean exceptionWasThrown = false;
        try
        {
            sut.validate(mockedContext);
        }
        catch (ValidationException vex)
        {
            exceptionWasThrown = true;
            assertEquals(1, vex.getErrors().size());
            assertTrue(vex.getErrors().containsKey("invalidTab"));
        }

        assertTrue(exceptionWasThrown);

        context.assertIsSatisfied();
    }

    /**
     * Helper method to setup common expectations.
     */
    private void setupGadgetExpectations()
    {
        context.checking(new Expectations()
        {
            {
                allowing(testGadgetOne).getZoneNumber();
                will(returnValue(0));

                allowing(testGadgetOne).getZoneIndex();
                will(returnValue(0));

                allowing(testGadgetTwo).getZoneNumber();
                will(returnValue(1));

                allowing(testGadgetTwo).getZoneIndex();
                will(returnValue(0));

                allowing(testGadgetThree).getZoneNumber();
                will(returnValue(1));

                allowing(testGadgetThree).getZoneIndex();
                will(returnValue(1));
            }
        });
    }
}
