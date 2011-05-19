package org.eurekastreams.web.client.ui.common.charts;

import com.googlecode.gchart.client.GChart;

public class StreamAnalyticsChart extends GChart
{
    public StreamAnalyticsChart()
    {
        setChartSize(300, 200);

        setBackgroundColor("#111111");
        setGridColor("#252525");
        addCurve(0);
        addCurve(1);
        
        int fillSpacing = 0;
        
        getYAxis().setTickLabelFontColor("#c6c6c6");
        getXAxis().setTickLabelFontColor("#c6c6c6");
        
        getYAxis().setHasGridlines(true);
        getXAxis().setTickCount(4);

        getCurve(0).getSymbol().setSymbolType(SymbolType.LINE);
        getCurve(0).getSymbol().setBackgroundColor("#339966");
        getCurve(0).getSymbol().setBorderColor(TRANSPARENT_BORDER_COLOR);

        getCurve(1).getSymbol().setSymbolType(SymbolType.VBAR_SOUTHEAST);
        getCurve(1).getSymbol().setBackgroundColor("#339966");
        getCurve(1).getSymbol().setBorderColor(TRANSPARENT_BORDER_COLOR);
        getCurve(1).getSymbol().setBorderWidth(fillSpacing==0?1:0);
        getCurve(1).getSymbol().setWidth(fillSpacing);
        getCurve(1).getSymbol().setFillThickness(Math.max(1, fillSpacing));
        getCurve(1).getSymbol().setFillSpacing(fillSpacing);

        getCurve(0).addPoint(0,5);
        getCurve(0).addPoint(5,7);
        getCurve(0).addPoint(10,5);
        getCurve(0).addPoint(15,14);
        getCurve(0).addPoint(20,10);
        getCurve(0).addPoint(25,19);
        getCurve(0).addPoint(30,17);

        getCurve(1).addPoint(0,5);
        getCurve(1).addPoint(5,7);
        getCurve(1).addPoint(10,5);
        getCurve(1).addPoint(15,14);
        getCurve(1).addPoint(20,10);
        getCurve(1).addPoint(25,19);
        getCurve(1).addPoint(30,17);

     }
}
