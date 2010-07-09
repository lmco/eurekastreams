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
package org.eurekastreams.web.client.ui;

import junit.framework.Assert;

import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the history state.
 */
public class HistoryStateTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The subject under test.
     */
    private HistoryState sut;

    /**
     * The JSNI facade mock.
     */
    private WidgetJSNIFacadeImpl jsniMock;

    /**
     * Setup the test fixture.
     */
    @Before
    public final void setUp()
    {
        jsniMock = context.mock(WidgetJSNIFacadeImpl.class);

        context.checking(new Expectations()
        {
            {
                oneOf(jsniMock).getHistoryToken();
                will(returnValue("view?param1=val"));

                oneOf(jsniMock).urlDecode("param1");
                will(returnValue("param1"));

                oneOf(jsniMock).urlDecode("val");
                will(returnValue("val"));

                oneOf(jsniMock).addHistoryListener(with(any(HistoryState.class)));
            }
        });

        sut = new HistoryState(jsniMock);
    }

    /**
     * Test setting the value without persisting.
     */
    @Test
    public final void setValueNoPersistTest()
    {
        sut.setValue("key", "value", false);

        Assert.assertEquals("value", sut.getValue("key"));

        context.assertIsSatisfied();
    }

    /**
     * Test setting the value and persisting.
     */
    @Test
    public final void setValuePersistTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(jsniMock).urlEncode("view");
                will(returnValue("view"));

                oneOf(jsniMock).urlEncode("param1");
                will(returnValue("param1"));

                oneOf(jsniMock).urlEncode("val");
                will(returnValue("val"));

                oneOf(jsniMock).urlEncode("key");
                will(returnValue("key"));

                oneOf(jsniMock).urlEncode("value");
                will(returnValue("value"));

                oneOf(jsniMock).setHistoryToken("view?param1=val&key=value", true);
            }
        });

        sut.setValue("key", "value", true);

        Assert.assertEquals("value", sut.getValue("key"));

        context.assertIsSatisfied();
    }

    /**
     * Test removing a value and persisting.
     */
    @Test
    public final void removeValuePersistTest()
    {
        // insure there's something to remove
        sut.setValue("key", "value", false);

        context.checking(new Expectations()
        {
            {
                oneOf(jsniMock).urlEncode("view");
                will(returnValue("view"));

                oneOf(jsniMock).urlEncode("param1");
                will(returnValue("param1"));

                oneOf(jsniMock).urlEncode("val");
                will(returnValue("val"));

                oneOf(jsniMock).setHistoryToken("view?param1=val", true);
            }
        });

        sut.removeValue("key", true);

        Assert.assertEquals("", sut.getValue("key"));

        context.assertIsSatisfied();
    }

    /**
     * Test removing a value without persisting.
     */
    @Test
    public final void removeValueNoPersistTest()
    {
        // insure there's something to remove
        sut.setValue("key", "value", false);
        Assert.assertEquals("value", sut.getValue("key"));

        sut.removeValue("key", false);

        Assert.assertEquals("", sut.getValue("key"));
        context.assertIsSatisfied();
    }

    /**
     * Test removing all values and persisting.
     */
    @Test
    public final void removeValueAllPersistTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(jsniMock).urlEncode("view");
                will(returnValue("view"));

                oneOf(jsniMock).setHistoryToken("view", true);
            }
        });

        sut.removeValue("param1", true);

        Assert.assertEquals("", sut.getValue("key"));

        context.assertIsSatisfied();
    }

    /**
     * Test setting the view without persisting or resetting.
     */
    @Test
    public final void setViewNoPersistNoClearTest()
    {
        sut.setView("newview", false, false);

        context.checking(new Expectations()
        {
            {
                oneOf(jsniMock).urlDecode("newview");
                will(returnValue("newview"));
            }
        });

        Assert.assertEquals("val", sut.getValue("param1"));
        Assert.assertEquals("newview", sut.getView());

        context.assertIsSatisfied();
    }

    /**
     * Test setting the view resetting values and without persisting.
     */
    @Test
    public final void setViewNoPersistClearTest()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(jsniMock).urlDecode("newview");
                will(returnValue("newview"));
            }
        });

        sut.setView("newview", true, false);

        Assert.assertEquals("", sut.getValue("param1"));
        Assert.assertEquals("newview", sut.getView());

        context.assertIsSatisfied();
    }

    /**
     * Test setting the view with persisting and reseting the values.
     */
    @Test
    public final void setViewPersistClearTest()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(jsniMock).urlEncode("newview");
                will(returnValue("newview"));

                oneOf(jsniMock).urlDecode("newview");
                will(returnValue("newview"));

                oneOf(jsniMock).setHistoryToken("newview", true);
            }
        });

        sut.setView("newview", true, true);

        Assert.assertEquals("", sut.getValue("param1"));
        Assert.assertEquals("newview", sut.getView());

        context.assertIsSatisfied();
    }
}
