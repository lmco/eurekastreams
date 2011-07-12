/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.charts;

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.googlecode.gchart.client.GChart;

/**
 * Stream Analytics Chart.
 */
public class StreamAnalyticsChart extends GChart
{
    /**
     * Chart width.
     */
    private static final int CHART_WIDTH = 300;

    /**
     * Chart height.
     */
    private static final int CHART_HEIGHT = 200;

    /**
     * Constructor.
     */
    public StreamAnalyticsChart()
    {
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().analyticsChart());
        setChartSize(CHART_WIDTH, CHART_HEIGHT);
        
        setBackgroundColor("#111111");
        setGridColor("#252525");
        
        addCurve(0);
        addCurve(1);

        int fillSpacing = 0;

        
        getYAxis().setTickLabelFontColor("#c6c6c6");
        getXAxis().setTickLabelFontColor("#c6c6c6");

        getYAxis().setHasGridlines(true);

        
        getXAxis().setTickCount(0);
        getYAxis().setTickCount(0);

        getCurve(0).getSymbol().setSymbolType(SymbolType.VBAR_SOUTHEAST);
        getCurve(0).getSymbol().setBackgroundColor("#1D402F");
        getCurve(0).getSymbol().setBorderColor(TRANSPARENT_BORDER_COLOR);
        getCurve(0).getSymbol().setBorderWidth(fillSpacing == 0 ? 1 : 0);
        getCurve(0).getSymbol().setWidth(fillSpacing);
        getCurve(0).getSymbol().setFillThickness(Math.max(1, fillSpacing));
        getCurve(0).getSymbol().setFillSpacing(fillSpacing);

        getCurve(1).getSymbol().setSymbolType(SymbolType.LINE);
        getCurve(1).getSymbol().setBackgroundColor("#339966");
        getCurve(1).getSymbol().setBorderColor(TRANSPARENT_BORDER_COLOR);

    }

    /**
     * Add point.
     * 
     * @param x
     *            x coordinate;
     * @param y
     *            y coordinate;
     */
    public void addPoint(final double x, final double y)
    {
        getXAxis().addTick(x);
        getCurve(0).addPoint(x, y);
        getCurve(1).addPoint(x, y);
    }

    /**
     * Clear all points.
     */
    public void clearPoints()
    {
        getYAxis().clearTicks();
        getXAxis().clearTicks();
        getCurve(0).clearPoints();
        getCurve(1).clearPoints();
    }

    /**
     * Update the chart.
     */
    @Override
    public void update()
    {
        final double ceilMult = 1.5;
        getYAxis().clearTicks();

        double yMax = getYAxis().getDataMax() * ceilMult;

        double divBy6 = Math.ceil(yMax / 6);

        for (int i = 1; i <= 6; i++)
        {
            getYAxis().addTick(divBy6 * i);
        }

        super.update();
    }
}
