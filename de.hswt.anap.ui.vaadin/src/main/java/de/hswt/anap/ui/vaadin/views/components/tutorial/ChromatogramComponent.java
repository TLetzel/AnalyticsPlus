/*
 * Copyright 2015-2017 the original author or authors.
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

package de.hswt.anap.ui.vaadin.views.components.tutorial;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Axis;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Lang;
import com.vaadin.addon.charts.model.LayoutDirection;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.Marker;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.PointReduceUtil;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.handler.payload.ResetPayload;
import de.hswt.hplcsl.Chromatogram;
import de.hswt.hplcsl.TimeSeries;
import de.hswt.hplcsl.internal.Point;

@SpringComponent
@ViewScope
public class ChromatogramComponent extends FixedContainerComponent {

	private static final long serialVersionUID = -697927714845337392L;

	private static final int GRAPH_TARGET_PIXEL_WIDTH = 800;

	@Autowired
	private SharedModelProvider definitionProvider;

	private Chart chart;

	private PointReduceUtil pointReduceUtil = new PointReduceUtil();

	@Override
	protected Component getContent() {
		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();
		contentLayout.addStyleName(CustomValoTheme.PADDING);

		chart = new Chart();
		chart.getConfiguration().setTitle("");
		chart.setImmediate(true);
		chart.setHeight("100%");
		contentLayout.addComponent(chart);

		return contentLayout;
	}

	private Configuration createChartConfiguration() {
		Configuration configuration = new Configuration();
		configuration.getTitle().setText("");

		PlotOptionsLine plotOptionsLine = new PlotOptionsLine();
		plotOptionsLine.setMarker(new Marker(false));
		plotOptionsLine.setShadow(false);
		plotOptionsLine.setAnimation(false);
		plotOptionsLine.setTurboThreshold(200000);
		configuration.setPlotOptions(plotOptionsLine);

		Axis yAxis = configuration.getyAxis();
		yAxis.setMin(0);
		yAxis.setTitle(new Title("Signal"));
		yAxis.getTitle().setVerticalAlign(VerticalAlign.HIGH);
		Axis xAxis = configuration.getxAxis();
		xAxis.setTitle("Time (min)");

		Legend legend = configuration.getLegend();
		legend.setLayout(LayoutDirection.VERTICAL);
		legend.setHorizontalAlign(HorizontalAlign.RIGHT);
		legend.setVerticalAlign(VerticalAlign.TOP);
		legend.setX(-10d);
		legend.setY(100d);
		legend.setBorderWidth(0);

		// TODO format tooltip, time and signal value + series name
		// only one digit fpr all number values
		// configuration
		// .getTooltip()
		// .setFormatter(
		// "''+ this.series.name +''+this.x +': '+ this.y +'C'");

		return configuration;
	}

	private void updateChromatogram(Chromatogram chromatogram) {
		Configuration configuration = createChartConfiguration();

		List<TimeSeries> compoundTimeSeries = chromatogram
				.getCompoundsTimeSeries();

		for (TimeSeries timeSeries : compoundTimeSeries) {
			DataSeries dataSeries = new DataSeries();
			dataSeries.setPlotOptions(new PlotOptionsSpline());
			if (timeSeries.getCompound() != null) {
				dataSeries.setName(timeSeries.getCompound().getCompoundName());
			}
			List<Point> points = timeSeries.getSeries();
			List<DataSeriesItem> dataSeriesItem = new ArrayList<DataSeriesItem>();
			for (int j = 0; j < points.size(); j++) {
				dataSeriesItem.add(new DataSeriesItem(points.get(j).getX(),
						points.get(j).getY()));
			}
			dataSeries.setData(dataSeriesItem);
			pointReduceUtil.ramerDouglasPeuckerReduce(dataSeries,
					GRAPH_TARGET_PIXEL_WIDTH);

			configuration.addSeries(dataSeries);
			Tooltip tooltip = new Tooltip();
			tooltip.setFormatter("function()  { "
					+ "return "
					+ "'<b>'+this.series.name +'</b><br/>"
					+ "<strong>Signal value : </strong> '+ Highcharts.numberFormat(this.y,1)"
					+ " +' <span> micromoles/liter</span> '+'<br/>"
					+ "<strong>Time : </strong>'+ Highcharts.numberFormat(this.x, 1) "
					+ "+' <span> minutes </span>';"
					+ "}");
			configuration.setTooltip(tooltip);
		}

		chart.drawChart(configuration);
	}

	private void resetChromatogram() {
		Configuration configuration = createChartConfiguration();
		chart.drawChart(configuration);
	}

	@Override
	protected String getTitle() {
		return "Chromatogram";
	}

	@EventBusListenerMethod
	private void handleChromatogramChange(Chromatogram chromatogram) {
		updateChromatogram(chromatogram);
	}

	@EventBusListenerMethod
	private void handleReset(ResetPayload reset) {
		resetChromatogram();
	}
}
