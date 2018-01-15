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

import java.util.List;

import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;
import de.hswt.anap.ui.vaadin.handler.payload.ResetPayload;
import de.hswt.hplcsl.Chromatogram;
import de.hswt.hplcsl.Compound;
import de.hswt.hplcsl.TimeSeries;

@SpringComponent
@ViewScope
public class CompoundsComponent extends FixedContainerComponent {

	private static final long serialVersionUID = -3300682735912407893L;

	private BeanItemContainer<TimeSeries> timeSeriesContainer;

	private Grid compoundsGrid;

	public CompoundsComponent() {
		super();

		timeSeriesContainer = new BeanItemContainer<>(TimeSeries.class);		
		timeSeriesContainer.addBean(getDefaultTimeSeries());
		timeSeriesContainer.addNestedContainerProperty("compound.compoundName");
		timeSeriesContainer.addNestedContainerProperty("compound.concentration");
		timeSeriesContainer.addNestedContainerProperty("compound.k");
		timeSeriesContainer.addNestedContainerProperty("compound.retentionTime");
		timeSeriesContainer.addNestedContainerProperty("compound.sigma");
		timeSeriesContainer.addNestedContainerProperty("compound.w");
		timeSeriesContainer.removeAllItems();
	}
	
	@Override
	protected Component getContent() {
		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);
		compoundsGrid = new Grid(timeSeriesContainer);
		compoundsGrid.setHeightMode(HeightMode.CSS);
		compoundsGrid.setSizeFull();
		compoundsGrid.setSelectionMode(SelectionMode.SINGLE);
		compoundsGrid.removeAllColumns();
		compoundsGrid.addColumn("compound.compoundName").setHeaderCaption("Compounds").setExpandRatio(16);
		compoundsGrid.addColumn("compound.concentration").setHeaderCaption("Concentration (" + UiConstants.UTF8_MICRO + "M)").setExpandRatio(16);
		compoundsGrid.addColumn("compound.k").setHeaderCaption("k").setExpandRatio(16);
		compoundsGrid.addColumn("compound.retentionTime").setHeaderCaption("Retention time (min)").setExpandRatio(16);
		compoundsGrid.addColumn("compound.sigma").setHeaderCaption("sigma").setExpandRatio(16);
		compoundsGrid.addColumn("compound.w").setHeaderCaption("W (pmol)").setExpandRatio(16);
		
		contentLayout.addComponent(compoundsGrid);

		return contentLayout;
	}

	@Override
	protected String getTitle() {
		return "Compounds";
	}
	
	private TimeSeries getDefaultTimeSeries() {
		Compound compound = new Compound();
		compound.setCompoundName("");
		return new TimeSeries(compound);
	}
	
	private void updateGrid(Chromatogram chromatogram) {
		timeSeriesContainer.removeAllItems();

		if (chromatogram == null) {
			return;
		}
		
		List<TimeSeries> timeSeries = chromatogram.getCompoundsTimeSeries();

		if (timeSeries == null || timeSeries.isEmpty()) {
			return;
		}

		timeSeriesContainer.addAll(timeSeries);
	}
	
	@EventBusListenerMethod
	private void handleChromatogramChange(Chromatogram chromatogram) {
		updateGrid(chromatogram);
	}
	
	@EventBusListenerMethod
	private void handleChromatogramChange(ResetPayload payload) {
		updateGrid(null);
	}
}
