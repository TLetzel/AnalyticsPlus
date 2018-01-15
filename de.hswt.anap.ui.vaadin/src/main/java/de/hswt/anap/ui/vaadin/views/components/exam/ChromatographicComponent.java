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
import de.hswt.anap.ui.vaadin.views.components.tutorial.ChromatographicPropertiesComponent;

@SpringComponent
@ViewScope
public class ChromatographicComponent extends ContainerComponent {

	private static final long serialVersionUID = -1486371016168618926L;

	@Autowired
	private IStandardsService standarService;

	@Autowired
	private ComponentFactory componentFactory;

	@Autowired
	private SharedModelProvider sharedModelProvider;

	@PropertyId("injectionVolume")
	private ComboBox injectionVolumeComboBox;

	@PropertyId("flowRate")
	private ComboBox flowRateComboBox;

	private Experiment experiment;

	private CssLayout contentLayout;

	private BeanFieldGroup<Experiment> fieldGroup;

	private static final Logger LOG = LoggerFactory
			.getLogger(ChromatographicPropertiesComponent.class);

	@Override
	protected Component getContent() {

		fieldGroup = new BeanFieldGroup<>(Experiment.class);
		fieldGroup.setReadOnly(false);

		experiment = sharedModelProvider.getExperiment();

		contentLayout = new CssLayout();
		contentLayout.setWidth("100%");

		updateEnabled(false);

		// Setting injection volume ComboBox
		injectionVolumeComboBox = componentFactory
				.createComboBox("Injection volume");
		for (int i = 1; i < 11; i++) {
			injectionVolumeComboBox.addItem(i * 10.0);
		}
		injectionVolumeComboBox
				.addValueChangeListener(e -> handleInjectionVolumeValueChanged());

		Label injectionVolumnUnitLabel = new Label(UiConstants.UTF8_MICRO + "L");
		injectionVolumnUnitLabel.setCaption(""); // layout issue
		injectionVolumnUnitLabel.setWidth("2.5rem");

		contentLayout.addComponent(componentFactory.createRowLayout(
				injectionVolumeComboBox, injectionVolumnUnitLabel));

		// Setting flow rate ComboBox
		flowRateComboBox = componentFactory.createComboBox("Flow rate");
		for (int i = 1; i < 201; i++) {
			flowRateComboBox.addItem(i * 10.0);
		}
		flowRateComboBox.setWidth("100%");
		flowRateComboBox
				.addValueChangeListener(e -> handleFlowRateValueChanged());

		Label flowRateUnitLabel = new Label(UiConstants.UTF8_MICRO + "L/min");
		flowRateUnitLabel.setCaption(""); // layout issue
		flowRateUnitLabel.setWidth("2.5rem");

		contentLayout.addComponent(componentFactory.createRowLayout(
				flowRateComboBox, flowRateUnitLabel));

		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(experiment);

		return contentLayout;
	}

	@Override
	protected String getTitle() {
		return "Chromatographic properties";
	}

	private void handleInjectionVolumeValueChanged() {
		injectionVolumeComboBox.commit();
		handleExperimentUpdate();
	}

	private void handleFlowRateValueChanged() {
		flowRateComboBox.commit();
		handleExperimentUpdate();
	}

	private void handleExperimentUpdate() {
		if (experiment.getFlowRate() == null
				|| experiment.getInjectionVolume() == null) {
			return;
		}
		LOG.debug("Publish event inside ChromatographcComponent with payload: "
				+ experiment);
		eventBus.publish(this, new ValidationRequestPayload(experiment,
				RuleType.INJECTION_VOLUME_FLOWRATE));
	}

	@EventBusListenerMethod
	private void handleValidationResultUpdate(ValidationResultPayload payload) {
		LOG.debug(
				"Receive event inside ChomratographicComponent experiment has changed, with payload: ",
				payload);
		boolean enabled = ExamFlowController.isEnabled(
				payload.getValidationResults(),
				RuleType.INJECTION_VOLUME_FLOWRATE,
				payload.getCurrentRuleType());

		updateEnabled(enabled);
	}

	private void updateEnabled(boolean enabled) {
		contentLayout.setEnabled(enabled);
		setHeaderColorStyle(CustomValoTheme.BACKGROUND_COLOR_DISABLED, !enabled);
	}
}
