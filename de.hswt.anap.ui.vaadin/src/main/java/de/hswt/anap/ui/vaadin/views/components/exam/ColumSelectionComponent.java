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

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import de.hswt.anap.model.ColumnConfiguration;
import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.factories.ComponentFactory;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationRequestPayload;

@SpringComponent
@ViewScope
public class ColumSelectionComponent extends ContainerComponent {

	private static final long serialVersionUID = 200371014635105849L;

	@Autowired
	private ComponentFactory componentFactory;

	@Autowired
	private IStandardsService standardService;

	@Autowired
	private SharedModelProvider sharedModelProvider;

	@PropertyId("column")
	private ComboBox columnComboBox;

	private Experiment experiment;

	private CssLayout contentLayout;

	private BeanFieldGroup<Experiment> fieldGroup;

	private static final Logger LOG = LoggerFactory
			.getLogger(ColumSelectionComponent.class);

	@Override
	protected Component getContent() {

		fieldGroup = new BeanFieldGroup<>(Experiment.class);
		fieldGroup.setReadOnly(false);

		experiment = sharedModelProvider.getExperiment();

		contentLayout = new CssLayout();
		contentLayout.setWidth("100%");

		columnComboBox = componentFactory.createComboBox("Column selection");
		columnComboBox.setContainerDataSource(new BeanItemContainer<>(
				ColumnConfiguration.class));
		columnComboBox.setItemCaptionPropertyId("name");
		columnComboBox.addItems(standardService.getAvailableColumns());
		columnComboBox.addValueChangeListener(e -> handleColumnChange());
		contentLayout.addComponent(componentFactory.createRowLayout(columnComboBox));

		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(experiment);

		columnComboBox.select(columnComboBox.getItemIds().iterator().next());

		return contentLayout;
	}

	private void handleColumnChange() {
		columnComboBox.commit();
		// needed to reset ui
		experiment.setSubstances(null);
		handleExperimentUpdate();
	}

	private void handleExperimentUpdate() {
		if (experiment.getColumn() == null) {
			return;
		}
		LOG.debug(
				"Publish event inside ColumnSelectionComponent experiment changed wiht payload: ",
				experiment);
		eventBus.publish(this, new ValidationRequestPayload(experiment,
				RuleType.COLUMN_CHANGE));
	}

	@Override
	protected String getTitle() {
		return "Column selection";
	}

}
