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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.handler.payload.ResetPayload;
import de.hswt.hplcsl.Chromatogram;
import de.hswt.hplcsl.Definition;
import de.hswt.hplcsl.Results;

@SpringComponent
@ViewScope
public class ResultsComponent extends FixedContainerComponent {

	private static final long serialVersionUID = 2257405211401122528L;

	private static final String COLUMN_PARAMETER = "parameter";

	private static final String COLUMN_VALUE = "value";

	private static final String DWELL_VOLUME = "Dwell volume [" + UiConstants.UTF8_MICRO + "l" + "]";

	private static final String DWELL_TIME = "Dwell time [min]";

	private static final String HETP = "HETP [cm]";

	private static final String BACKPRESSURE = "Backpressure [bar]";

	private static final String ELUENT_VISCOSITY = "Eluent viscosity [cP]";

	private static final String THEORETICAL_PLATES = "Theoretical plates";

	private static final String REDUCED_PLATE_HEIGHT = "Reduced plate height";
	
	@Autowired
	private SharedModelProvider definitionProvider;

	private BeanContainer<String, ContentWrapper> resultsContainer;

	private Grid resultsGrid;

	private NumberFormat fourDigitsFormatter = new DecimalFormat("#0.0000");

	private NumberFormat threeDigetsFormatter = new DecimalFormat("#0.00");
	
	private NumberFormat integerFormatter = new DecimalFormat("#0");
	
	private DecimalFormat decimalFormatter = new DecimalFormat("0.000E0");
	
	private Definition definition;

	@Override
	protected Component getContent() {
		definition = definitionProvider.getDefinition();
		resultsContainer = new BeanContainer<>(ContentWrapper.class);
		resultsContainer.setBeanIdProperty("parameter");
		createValues();

		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);

		resultsGrid = new Grid(resultsContainer);
		resultsGrid.setHeightMode(HeightMode.CSS);
		resultsGrid.setSizeFull();
		resultsGrid.removeAllColumns();
		resultsGrid.addColumn(COLUMN_PARAMETER).setHeaderCaption("Parameter");
		resultsGrid.addColumn(COLUMN_VALUE).setHeaderCaption("Value");
		resultsGrid.setContainerDataSource(resultsContainer);

		contentLayout.addComponent(resultsGrid);

		return contentLayout;
	}

	private void createValues() {
		resultsContainer.addBean(new ContentWrapper(DWELL_VOLUME, ""));
		resultsContainer.addBean(new ContentWrapper(DWELL_TIME, ""));
		resultsContainer.addBean(new ContentWrapper(HETP, ""));
		resultsContainer.addBean(new ContentWrapper(THEORETICAL_PLATES, ""));
		resultsContainer.addBean(new ContentWrapper(BACKPRESSURE, ""));
		resultsContainer.addBean(new ContentWrapper(ELUENT_VISCOSITY, ""));
		resultsContainer.addBean(new ContentWrapper(REDUCED_PLATE_HEIGHT, ""));
	}

	@SuppressWarnings("unchecked")
	private void updateResultValues(Chromatogram chromatogramm) {
		String dwellVolume = "";
		String dwellTime = "";
		String hetp = "";
		String theoreticalPlates = "";
		String backPreasure = "";
		String eluentViscosity = "";
		String reducedPlateHeight = "";
		
		if (chromatogramm != null) {
			Results results = chromatogramm.getResults();
			if(definition.isGradient()){
				dwellVolume = Double.toString(results.getDwellVolume());
				dwellTime = Double.toString(results.getDwellTime());
				eluentViscosity = " -- ";
			}else{
				dwellVolume = " -- ";
				dwellTime = " -- ";
				eluentViscosity = fourDigitsFormatter.format(results.getEluentViscosity());
			}
			hetp = decimalFormatter.format(results.getHetp());
			theoreticalPlates = integerFormatter.format(results.getTheoreticalPlates());
			backPreasure = threeDigetsFormatter.format(results.getBackpressure());
			reducedPlateHeight = fourDigitsFormatter.format(results.getReducedPlateHeight());
		}
		
		resultsContainer.getItem(DWELL_VOLUME).getItemProperty(COLUMN_VALUE)
				.setValue(dwellVolume);
		resultsContainer.getItem(DWELL_TIME).getItemProperty(COLUMN_VALUE)
				.setValue(dwellTime);
		resultsContainer.getItem(HETP).getItemProperty(COLUMN_VALUE)
				.setValue(hetp);
		resultsContainer.getItem(THEORETICAL_PLATES)
				.getItemProperty(COLUMN_VALUE)
				.setValue(theoreticalPlates);
		resultsContainer.getItem(BACKPRESSURE).getItemProperty(COLUMN_VALUE)
				.setValue(backPreasure);
		resultsContainer.getItem(ELUENT_VISCOSITY)
				.getItemProperty(COLUMN_VALUE)
				.setValue(eluentViscosity);
		resultsContainer.getItem(REDUCED_PLATE_HEIGHT)
				.getItemProperty(COLUMN_VALUE)
				.setValue(reducedPlateHeight);

		if (chromatogramm != null) {
			Results results = chromatogramm.getResults();
			if (results.getBackpressure() > 400) {
				Notification.show(
						"Backpressure is too heigh! Has to be lower or equal 400 bar",
						Type.WARNING_MESSAGE);
			} else if (results.getTheoreticalPlates() < 1000) {
				Notification.show("Number of theoretical plates is too low",
						Type.WARNING_MESSAGE);
			}
		}
	}
	
	@EventBusListenerMethod
	private void handleChromatogramChange(Chromatogram chromatogramm) {
		updateResultValues(chromatogramm);
	}
	
	@EventBusListenerMethod
	private void handleReset(ResetPayload payload) {
		updateResultValues(null);
	}

	@Override
	protected String getTitle() {
		return "Results";
	}

	public class ContentWrapper {

		private String parameter;

		private String value;

		public ContentWrapper(String parameter) {
			super();
			this.parameter = parameter;
		}

		public ContentWrapper(String parameter, String value) {
			super();
			this.parameter = parameter;
			this.value = value;
		}

		public String getParameter() {
			return parameter;
		}

		public void setParameter(String parameter) {
			this.parameter = parameter;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
