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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.hswt.anap.model.ColumnConfiguration;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.converter.RangedStringToDoubleConverter;
import de.hswt.anap.ui.vaadin.factories.ComponentFactory;
import de.hswt.hplcsl.Definition;

@SpringComponent
@ViewScope
public class ColumnPropertiesComponent extends ContainerComponent {

	private static final long serialVersionUID = 8190087125346763307L;

	private static final Logger LOG = LoggerFactory.getLogger(ColumnPropertiesComponent.class);

	@Autowired
	private ComponentFactory componentFactory;

	@Autowired
	private SharedModelProvider definitionProvider;

	@Autowired
	private IStandardsService standardsService;

	@PropertyId("aTerm")
	private TextField aTerm;

	@PropertyId("bTerm")
	private TextField bTerm;

	@PropertyId("cTerm")
	private TextField cTerm;

	@PropertyId("column.name")
	private ComboBox columnComboBox;

	@PropertyId("column.particleSize")
	private ComboBox particleSizeComboBox;

	@PropertyId("column.length")
	private ComboBox columnLengthComboBox;

	@PropertyId("column.diameter")
	private ComboBox columnDiameterComboBox;

	private Map<String, ColumnConfiguration> columns = new HashMap<>();

	private BeanFieldGroup<Definition> fieldGroup;
	
	private boolean initialized = false;

	private boolean activeUpdate = true;

	private Property.ValueChangeListener listener = new Property.ValueChangeListener() {

		private static final long serialVersionUID = 1177275771555611788L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if (event.getProperty() == columnDiameterComboBox) {
				handleColumnDiameterValueChanged();
			} else if (event.getProperty() == columnLengthComboBox) {
				handleColumnLengthValueChanged();
			} else if (event.getProperty() == particleSizeComboBox) {
				handleParticleSizeValueChanged();
			}
		}
	};

	@Override
	protected Component getContent() {
		// TODO replace column configuration with column from hplc lib

		fieldGroup = new BeanFieldGroup<>(Definition.class);
		fieldGroup.setReadOnly(false);

		// fields must be initialized first because of BeanFieldGroup with
		// nested
		// properties
		columnComboBox = componentFactory.createComboBox("Column selection (Stationary phase)");
		columnLengthComboBox = componentFactory.createComboBox("Length");
		columnDiameterComboBox = componentFactory.createComboBox("Inner diameter");
		particleSizeComboBox = componentFactory.createComboBox("Particle size");

		aTerm = new TextField("A:");
		bTerm = new TextField("B:");
		cTerm = new TextField("C:");

		// fields must bound before setting the item because of BeanFieldGroup
		// with nested
		// properties
		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(definitionProvider.getDefinition());

		String first = null;
		for (ColumnConfiguration column : standardsService.getAvailableColumns()) {
			columns.put(column.getName(), column);
			if (first == null) {
				first = column.getName();
			}
		}

		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();

		columnComboBox.addItems(columns.keySet());
		columnComboBox.setValue(definitionProvider.getDefinition().getColumn().getName());
		columnComboBox.addValueChangeListener(e -> handleColumnValueChanged());
		contentLayout.addComponent(componentFactory.createRowLayout(columnComboBox));

		columnLengthComboBox.addValueChangeListener(listener);

		Label columnLengthUnitLabel = new Label("mm");
		columnLengthUnitLabel.setCaption(""); // needed for layout
		columnLengthUnitLabel.setWidth("2rem");
		contentLayout.addComponent(componentFactory.createRowLayout(columnLengthComboBox, columnLengthUnitLabel));

		columnDiameterComboBox.addValueChangeListener(listener);

		Label columnDiameterUnitLabel = new Label("mm");
		columnDiameterUnitLabel.setCaption(""); // needed for layout
		columnDiameterUnitLabel.setWidth("2rem");
		contentLayout.addComponent(componentFactory.createRowLayout(columnDiameterComboBox, columnDiameterUnitLabel));

		particleSizeComboBox.addValueChangeListener(listener);

		Label particleSizeUnitLabel = new Label(UiConstants.UTF8_MICRO + "m");
		particleSizeUnitLabel.setCaption(""); // needed for layout
		particleSizeUnitLabel.setWidth("2rem");
		contentLayout.addComponent(componentFactory.createRowLayout(particleSizeComboBox, particleSizeUnitLabel));

		Label vanDeemterLabel = new Label("<u>Reduced Van Deemter terms<u>", ContentMode.HTML);
		contentLayout.addComponent(componentFactory.createRowLayout(vanDeemterLabel));

		initColumnProperties();

		Definition definition = definitionProvider.getDefinition();

		//TODO fix van Deemter including RangedStringToDoubleConverter
		
		aTerm.setImmediate(true);
		aTerm.setWidth("100%");
//		aTerm.setConverter(new RangedStringToDoubleConverter(aTerm, 0.00001, 9999999.0));
		aTerm.setValue(String.valueOf(definition.getATerm()));
		aTerm.addValueChangeListener(e -> handleATermValueChanged());
		contentLayout.addComponent(componentFactory.createRowLayout(aTerm));

		bTerm.setImmediate(true);
		bTerm.setWidth("100%");
//		bTerm.setConverter(new RangedStringToDoubleConverter(bTerm, 0.00001, 9999999.0));
		bTerm.setValue(String.valueOf(definition.getBTerm()));
		bTerm.addValueChangeListener(e -> handleBTermValueChanged());
		contentLayout.addComponent(componentFactory.createRowLayout(bTerm));

		cTerm.setImmediate(true);
		cTerm.setWidth("100%");
//		cTerm.setConverter(new RangedStringToDoubleConverter(cTerm, 0.00001, 9999999.0));
		cTerm.setValue(String.valueOf(definition.getCTerm()));
		cTerm.addValueChangeListener(e -> handleCTermValueChanged());
		contentLayout.addComponent(componentFactory.createRowLayout(cTerm));
		initialized = true;

		return contentLayout;
	}

	public void handleColumnValueChanged() {
		initColumnProperties();
		if (!initialized) {
			return;
		}

		columnComboBox.commit();
		handleValueChanged();
	}

	private void handleColumnLengthValueChanged() {
		if (!initialized) {
			return;
		}
		columnLengthComboBox.commit();
		handleValueChanged();
	}

	private void handleColumnDiameterValueChanged() {
		if (!initialized) {
			return;
		}

		columnDiameterComboBox.commit();
		handleValueChanged();
	}

	private void handleParticleSizeValueChanged() {
		if (!initialized) {
			return;
		}

		particleSizeComboBox.commit();
		handleValueChanged();
	}
	
	private void handleATermValueChanged() {
		if (!initialized) {
			return;
		}
//		aTerm.commit();
		//workaround without RangedStringToDoubleConverter
		definitionProvider.getDefinition().setATerm(Double.parseDouble(aTerm.getValue()));
		aTerm.setValue(String.valueOf(definitionProvider.getDefinition().getATerm()));
		handleValueChanged();
	}

	private void handleBTermValueChanged() {
		if (!initialized) {
			return;
		}
//		bTerm.commit();		
		//workaround without RangedStringToDoubleConverter
		definitionProvider.getDefinition().setBTerm(Double.parseDouble(bTerm.getValue()));
		bTerm.setValue(String.valueOf(definitionProvider.getDefinition().getBTerm()));
		handleValueChanged();
	}

	private void handleCTermValueChanged() {
		if (!initialized) {
			return;
		}
//		cTerm.commit();
		//workaround without RangedStringToDoubleConverter
		definitionProvider.getDefinition().setCTerm(Double.parseDouble(cTerm.getValue()));
		cTerm.setValue(String.valueOf(definitionProvider.getDefinition().getCTerm()));
		handleValueChanged();
	}

	private void handleValueChanged() {
		if (!activeUpdate) {
			return;
		}
		LOG.debug("publish event inside handleValueChanged with payload {}", definitionProvider.getDefinition());
		eventBus.publish(this, definitionProvider.getDefinition());
	}

	private void initColumnProperties() {
		activeUpdate = false;

		ColumnConfiguration column = columns.get(columnComboBox.getValue());

		Object lengthValue = columnLengthComboBox.getValue();
		Object diameterValue = columnDiameterComboBox.getValue();
		Object paricelSizeValue = particleSizeComboBox.getValue();

		columnLengthComboBox.removeValueChangeListener(listener);
		columnLengthComboBox.removeAllItems();
		columnLengthComboBox.addItems(column.getLengths());
		columnLengthComboBox.addValueChangeListener(listener);
		if (lengthValue == null || !columnLengthComboBox.containsId(lengthValue)) {
			columnLengthComboBox.select(column.getLengths().get(0));
		} else {
			columnLengthComboBox.select(lengthValue);
		}
		handleColumnLengthValueChanged();

		columnDiameterComboBox.removeValueChangeListener(listener);
		columnDiameterComboBox.removeAllItems();
		columnDiameterComboBox.addItems(column.getDiameters());
		columnDiameterComboBox.addValueChangeListener(listener);
		if (diameterValue == null) {
			columnDiameterComboBox.select(column.getDiameters().get(0));
		} else {
			columnDiameterComboBox.select(diameterValue);
		}
		handleColumnDiameterValueChanged();

		particleSizeComboBox.removeValueChangeListener(listener);
		particleSizeComboBox.removeAllItems();
		particleSizeComboBox.addItems(column.getParticleSizes());
		particleSizeComboBox.addValueChangeListener(listener);
		if (paricelSizeValue == null) {
			particleSizeComboBox.select(column.getParticleSizes().get(0));
		} else {
			particleSizeComboBox.select(paricelSizeValue);
		}
		handleParticleSizeValueChanged();

		activeUpdate = true;
	}

	@Override
	protected String getTitle() {
		return "Stationary phase properties (Column)";
	}
}
