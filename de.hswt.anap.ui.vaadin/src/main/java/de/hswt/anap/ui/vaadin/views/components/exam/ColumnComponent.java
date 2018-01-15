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

package de.hswt.anap.ui.vaadin.views.components.exam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.hswt.anap.model.ColumnConfiguration;
import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.ExamFlowController;
import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.factories.ComponentFactory;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationRequestPayload;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationResultPayload;

@SpringComponent
@ViewScope
public class ColumnComponent extends ContainerComponent {

	private static final long serialVersionUID = 8190087125346763307L;

	@Autowired
	private ComponentFactory componentFactory;

	@Autowired
	private IStandardsService standardsService;

	@Autowired
	private SharedModelProvider sharedModelProvider;

	@PropertyId("particleSize")
	private ComboBox particleSizeComboBox;

	@PropertyId("columnLength")
	private ComboBox columnLengthComboBox;

	@PropertyId("columnDiameter")
	private ComboBox columnDiameterComboBox;

	private Experiment experiment;

	private CssLayout contentLayout;

	private BeanFieldGroup<Experiment> fieldGroup;

	private static final Logger LOG = LoggerFactory
			.getLogger(ColumnComponent.class);

	@Override
	protected Component getContent() {

		fieldGroup = new BeanFieldGroup<>(Experiment.class);
		fieldGroup.setReadOnly(false);

		experiment = sharedModelProvider.getExperiment();

		contentLayout = new CssLayout();
		contentLayout.setWidth("100%");

		updateEnabled(false);
		// Setting column Length ComboBox
		columnLengthComboBox = componentFactory.createComboBox("Length");
		columnLengthComboBox
				.addValueChangeListener(e -> handleColumnLengthValueChanged());
		Label columnLengthUnitLabel = new Label("mm");
		columnLengthUnitLabel.setCaption(""); // needed for layout
		columnLengthUnitLabel.setWidth("2rem");

		contentLayout.addComponent(componentFactory.createRowLayout(
				columnLengthComboBox, columnLengthUnitLabel));

		// Setting column Diameter ComboBox
		columnDiameterComboBox = componentFactory
				.createComboBox("Inner diameter");
		columnDiameterComboBox
				.addValueChangeListener(e -> handleColumnDiameterValueChanged());
		Label columnDiameterUnitLabel = new Label("mm");
		columnDiameterUnitLabel.setCaption(""); // needed for layout
		columnDiameterUnitLabel.setWidth("2rem");

		// Setting particle size ComboBox
		particleSizeComboBox = componentFactory.createComboBox("Particle size");
		particleSizeComboBox
				.addValueChangeListener(e -> handleParticleSizeValueChanged());
		Label particleSizeUnitLabel = new Label(UiConstants.UTF8_MICRO + "m");
		particleSizeUnitLabel.setCaption(""); // needed for layout
		particleSizeUnitLabel.setWidth("2rem");

		contentLayout.addComponent(componentFactory.createRowLayout(
				columnDiameterComboBox, columnDiameterUnitLabel));
		contentLayout.addComponent(componentFactory.createRowLayout(
				particleSizeComboBox, particleSizeUnitLabel));

		checkForColumnChange();
		// Commit to set one value in defintion to null (Otherwise the created
		// definition is complete and will create a chromatogram although not
		// all Values are selected)
		// TODO to solve this problem, change initDefinition() in hplcSimulator
		columnLengthComboBox.commit();

		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(experiment);

		return contentLayout;
	}

	private void handleColumnLengthValueChanged() {
		columnLengthComboBox.commit();
		handleExperimentUpdate();
	}

	private void handleColumnDiameterValueChanged() {
		columnDiameterComboBox.commit();
		handleExperimentUpdate();
	}

	private void handleParticleSizeValueChanged() {
		particleSizeComboBox.commit();
		handleExperimentUpdate();
	}

	private void handleExperimentUpdate() {
		if (experiment.getColumnLength() == null
				|| experiment.getColumnDiameter() == null
				|| experiment.getParticleSize() == null) {
			return;
		}
		LOG.debug("Publish event in ColumnComponent with payload: "
				+ experiment);
		eventBus.publish(this, new ValidationRequestPayload(experiment,
				RuleType.THEORETICAL_PLATES));
	}

	@EventBusListenerMethod
	private void handleValidationResultUpdate(ValidationResultPayload payload) {

		LOG.debug(
				"Receive event in ColumnComponent experiment has changed with payload{}",
				payload);
		RuleType initRuleType = payload.getCurrentRuleType();

		// TODO can this work as expexted?
		// and what is expected?
		boolean enabled = ExamFlowController.isEnabled(
				payload.getValidationResults(), RuleType.BACK_PRESSURE,
				initRuleType)
				|| ExamFlowController.isEnabled(payload.getValidationResults(),
						RuleType.THEORETICAL_PLATES, initRuleType);

		updateEnabled(enabled);
	}

	private void updateEnabled(boolean enabled) {
		contentLayout.setEnabled(enabled);
		setHeaderColorStyle(CustomValoTheme.BACKGROUND_COLOR_DISABLED, !enabled);
	}

	/** Checks if the column changed, if so loads new column Properties. */
	private void checkForColumnChange() {

		ColumnConfiguration choosenColumn = experiment.getColumn();

		Object lengthValue = columnLengthComboBox.getValue();
		Object diameterValue = columnLengthComboBox.getValue();
		Object particelSizeValue = columnLengthComboBox.getValue();

		columnDiameterComboBox.removeAllItems();
		columnDiameterComboBox.addItems(choosenColumn.getDiameters());
		if (diameterValue != null) {
			columnDiameterComboBox.select(diameterValue);
		}

		columnLengthComboBox.removeAllItems();
		columnLengthComboBox.addItems(choosenColumn.getLengths());

		if (lengthValue != null
				&& choosenColumn.getLengths().contains(lengthValue)) {
			columnLengthComboBox.select(lengthValue);
		}

		particleSizeComboBox.removeAllItems();
		particleSizeComboBox.addItems(choosenColumn.getParticleSizes());

		if (particelSizeValue != null) {
			particleSizeComboBox.select(particelSizeValue);
		}
	}

	@Override
	protected String getTitle() {
		return "Column properties";
	}

}
