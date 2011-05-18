package org.eurekastreams.web.client.ui.common.charts;
import com.googlecode.gchart.client.GChart;

public class StreamAnalyticsChart extends GChart
{
    public StreamAnalyticsChart()
    {
        setChartSize(350, 200);

        setBackgroundColor("#111111");
        setGridColor("#252525");
        addCurve();
        
        getYAxis().setHasGridlines(true);
        getCurve().getSymbol().setSymbolType(SymbolType.HBAR_BASELINE_CENTER);
        getCurve().getSymbol().setFillSpacing(0);
        getCurve().getSymbol().setFillThickness(1);
        getCurve().getSymbol().setHeight(0);
        getCurve().getSymbol().setWidth(0);
        getCurve().getSymbol().setBaseline(0);
        getCurve().getSymbol().setBackgroundColor("rgba(0,0,255,0.3)");
        
        for (int i = 0; i < 10; i++) 
          getCurve().addPoint(i,i*i);


     }

}
